package x64codegen;

import x86codegen.*;
import lowlevel.*;
import java.util.LinkedList;
import java.util.Iterator;
import dataflow.BitArraySet;

public class X64RegisterAllocator {

    private CodeItem firstItem;
    int availableRegs;
    private BitArraySet[] liveRanges;
    private BitArraySet[] physicalLiveRanges;
    private int[][] interferenceGraph;
    private LinkedList allocatedRegs;
    private LinkedList spilledRegs;
    private int[] regMap;
    private boolean[] usedRegs;

    public X64RegisterAllocator(CodeItem first, int numRegs) {
        firstItem = first;
        availableRegs = numRegs;
    }

    public void performAllocation() {
        // opers are now annotated with liveness, so we can compute live ranges
        // from them
        // from live ranges, we compute interference graph and do register
        // allocation.  The difficulty arises with the fact that some x86
        // registers are already in use.  If we allocate a variable to register,
        // we must be sure the live range of existing x86 registers doesn't
        // conflict with this register

        for (CodeItem currItem = firstItem; currItem != null;
                currItem = currItem.getNextItem()) {
            if (currItem instanceof Data) {
                continue;
            }
            Function func = (Function) currItem;

            computeLiveRanges(func);

            // remove opers where reg defined isn't used
            removeUnusedDefs(func);

            computeInterferenceGraph(func);
            // make lists of regs spilled and allocated

            determineAllocationOrderAndSpills(func);
            // makes BitArraySet for 8 physical regs, for use by assignRegisters
            computeLiveRangesForPhysicalRegs(func);
            // for regs chosen for allocation, try to find an avail physical regs
            assignPhysicalRegs(func);
            // inside opers, convert virtual regs to x86 physical regs
            annotateRegisters(func);
            // need to insert push and pop for callee save, and adjust offsets to
            // incoming params
            updateForCalleeSave(func);
            // for regs chosen for spill, insert spill code
            annotateSpills(func);

            // check that no virtual regs still exist
            checkForUnallocatedRegs(func);
        }

    }

    private void computeLiveRanges(Function func) {
        // we have liveness of variables - each oper knows the regs which are
        // live in it.  We need to convert this to a set of regs, each of which
        // knows which opers it is live in
        // We simply walk the opers adding them to appropriate reg set

        //create and initialize liveRange sets
        liveRanges = new BitArraySet[func.getMaxRegNum() + 1];
        for (int i = 0; i < func.getMaxRegNum() + 1; i++) {
            liveRanges[i] = new BitArraySet(func.getMaxOperNum() + 1);
        }

        for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                currBlock = currBlock.getNextBlock()) {
            for (Operation currOper = currBlock.getLastOper(); currOper != null;
                    currOper = currOper.getPrevOper()) {
                // for every oper with vars live in it
                BitArraySet operLiveRange = currOper.getLiveRange();
                if (!operLiveRange.isEmpty()) {
                    for (int i = 0; i <= operLiveRange.getMaxVal(); i++) {
                        if (operLiveRange.contains(i)) {
                            liveRanges[i].add(currOper.getNum());
                        }
                    }
                }
            }
        }

        for (int i = 0; i < func.getMaxRegNum() + 1; i++) {
            if (!liveRanges[i].isEmpty()) {
                System.out.println("For reg " + i + " live range is:");
                for (int j = 0; j < func.getMaxOperNum() + 1; j++) {
                    if (liveRanges[i].contains(j)) {
                        System.out.print("  " + j);
                    }
                }
                System.out.println();
            }
        }
    }

    private void removeUnusedDefs(Function func) {
        // looking for reg defs which aren't in the liverange of the next oper
        for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                currBlock = currBlock.getNextBlock()) {
            Operation nextOper = null;
            for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                    currOper = nextOper) {
                // we do this manually because a delete might mess up the list
                nextOper = currOper.getNextOper();
                // check all dests before delete
                boolean canDelete = true;
                boolean foundRegDest = false;
                for (int i = 0; i < Operation.MAX_DEST_OPERANDS; i++) {
                    Operand currOperand = currOper.getDestOperand(i);
                    if ((currOperand != null) &&
                            (currOperand.getType() == Operand.OperandType.REGISTER)) {
                        foundRegDest = true;
                        int regNum = ((Integer) currOperand.getValue()).intValue();
                        Operation subsequentOper;
                        if (currOper.getNextOper() == null) {
                            BasicBlock nextBlock = currBlock.getNextBlock();
                            while (nextBlock != null && nextBlock.getFirstOper() == null) {
                                nextBlock = nextBlock.getNextBlock();
                            }
                            // now, if nextBlock != null, use its first oper
                            if (nextBlock == null) {
                                subsequentOper = null;
                            }
                            else {
                                subsequentOper = nextBlock.getFirstOper();
                            }
                        }
                        else {
                            subsequentOper = currOper.getNextOper();
                        }
                        if ((subsequentOper != null) &&
                                subsequentOper.getLiveRange().contains(regNum)) {
                            canDelete = false;
                            break;
                        }
                    }
                    else if (currOperand != null) {
                        canDelete = false;
                    }
                }
                if (foundRegDest && canDelete) {
                    System.out.println("RegAlloc Deleted Oper #" + currOper.getNum());
                    currOper.delete();
                }
            }
        }
    }

    private void computeInterferenceGraph(Function func) {
        // first, we initialize the graph (a 2D matrix)
        int maxReg = func.getMaxRegNum();
        interferenceGraph = new int[maxReg + 1][maxReg + 1];

        // we use the liveRanges to compute the interference graph
        // for efficiency, we only compute traverse the diagonal, but fill in
        // both halves of graph
        for (int i = 0; i <= maxReg; i++) {
            for (int j = 0; j <= i; j++) {
                // intersect liveRanges of regs; if not empty, then conflict
                if (!liveRanges[i].intersect(liveRanges[j]).isEmpty()) {
                    interferenceGraph[i][j] = 1;
                    interferenceGraph[j][i] = 1;
                    System.out.print("1  ");
                }
                else {
                    interferenceGraph[i][j] = 0;
                    interferenceGraph[j][i] = 0;
                    System.out.print("0  ");
                }
            }
            System.out.println();
        }
    }

    private void determineAllocationOrderAndSpills(Function func) {
        // this is the main routine which does register allocation
        // it will divide registers into 2 piles: those allocated and those to
        // be spilled.  Allocated regs will be inserted into an ordered list,
        // such that the first one added will be at the tail of the list

        // we need various structures to support register allocation
        // we need lists of allocated and spilled (instance vars)
        // we need a list of undecided regs (just an array)

        allocatedRegs = new LinkedList();
        spilledRegs = new LinkedList();
        int maxRegNum = func.getMaxRegNum();
        int numConflictingRegs = 0;

        boolean[] isUndecidedReg = new boolean[maxRegNum + 1];
        for (int i = 0; i <= maxRegNum; i++) {
            boolean hasConflict = false;
            for (int j = 0; j <= i; j++) {
                // here we count a register conflicting with itself as a conflict,
                // because we just want to know if it exists at this point
                if (interferenceGraph[i][j] != 0) {
                    hasConflict = true;
                    break;
                }
            }
            // if conflict, then it goes in undecided array
            isUndecidedReg[i] = hasConflict;
            if (hasConflict) {
                numConflictingRegs++;
            }
        }
        // so we don't have to keep counting the number of conflicts a reg has,
        // we keep a count and update it as allocation goes on
        // note: here we don't count a conflict with yourself as a conflict
        int[] numConflicts = new int[maxRegNum + 1];
        for (int i = 0; i <= maxRegNum; i++) {
            int numConflict = 0;
            for (int j = 0; j <= maxRegNum; j++) {
                if ((i != j) && (interferenceGraph[i][j] != 0)) {
                    numConflict++;
                }
            }
            numConflicts[i] = numConflict;
        }

        // now we iterate, pulling off unconstrained regs
        // the cycles go like this:
        //    a. iterate till nothing is unconstrained
        //    b. spill the most constrained reg (this heuristic could be improved
        //        later)
        //    c. repeat until all regs either allocated or spilled

        int oldNumConflicts;

        while (numConflictingRegs > 0) {

            oldNumConflicts = numConflictingRegs;
            boolean somethingPulledOff = true;

            while (somethingPulledOff && (numConflictingRegs > 0)) {
                somethingPulledOff = false;

                for (int i = 0; i <= maxRegNum; i++) {
                    if (isUndecidedReg[i]) {
                        if (numConflicts[i] < availableRegs) {
                            allocatedRegs.addFirst(new Integer(i));
                            isUndecidedReg[i] = false;
                            numConflictingRegs--;
                            // now we need to update conflicts with other regs
                            for (int j = 0; j <= maxRegNum; j++) {
                                if (interferenceGraph[i][j] != 0) {
                                    numConflicts[j]--;
                                }
                            }
                            somethingPulledOff = true;
                        }
                    }
                }
            }
            // now, if we have unallocated Regs, we have a problem and need to spill
            // For now, we will choose to spill the most constrained register left
            if (numConflictingRegs > 0) {
                int mostConstrainedReg = 0;
                int mostConstraints = 0;
                for (int i = 0; i <= maxRegNum; i++) {
                    if (isUndecidedReg[i]) {
                        if (numConflicts[i] > mostConstraints) {
                            mostConstraints = numConflicts[i];
                            mostConstrainedReg = i;
                        }
                    }
                }
                // we've found most constrained - move to spilled
                spilledRegs.add(new Integer(mostConstrainedReg));
                isUndecidedReg[mostConstrainedReg] = false;
                numConflictingRegs--;
                // now we need to update conflicts with other regs
                for (int i = 0; i <= maxRegNum; i++) {
                    if (interferenceGraph[mostConstrainedReg][i] != 0) {
                        numConflicts[i]--;
                    }
                }
            }

            // numConflictingRegs should have decreased during this interation,
            // either due to allocation or spills.  If not, problem!
            if (numConflictingRegs == oldNumConflicts) {
                throw new X86CodegenException("RegAlloc: loop didn't allocate regs!");
            }
        }

        Iterator allocateIterator = allocatedRegs.iterator();
        System.out.println("Allocated regs:");
        while (allocateIterator.hasNext()) {
            int regNum = ((Integer) allocateIterator.next()).intValue();
            System.out.print("   " + regNum);
        }
        System.out.println();
        System.out.println();

        Iterator spillIterator = spilledRegs.iterator();
        System.out.println("Spilled regs:");
        while (spillIterator.hasNext()) {
            int regNum = ((Integer) spillIterator.next()).intValue();
            System.out.print("   " + regNum);
        }
        System.out.println();
        System.out.println();
    }

    private void computeLiveRangesForPhysicalRegs(Function func) {
        // here we want to capture live ranges for physical regs which have
        // already been allocated - e.g., use of EAX/EDX for mult/div

        // we assume that the live ranges for these physical regs will only be
        // within a BB, so we just use the same approach from liveness analysis
        // for analyzing within a block

        // but new problem is that the live range of any use of esi/edi or other
        // parameter reg is only till the JSR
        // approach will be for JSR to be a use of all 6 parameter regs, and they will
        // remain used (backwards) till definition
        // VERY conservative ....


        // the current live set had better equal livenessIn, or we are confused

        // to support caller save, we want to make the caller save registers live
        // during the JSR.  But we want the live range to ONLY be the JSR, so if
        // the oper just done was a JSR, we need to turn off use of those regs
        boolean lastOperWasJSR = false;

        physicalLiveRanges = new BitArraySet[availableRegs + 1];
        for (int i = 0; i < availableRegs + 1; i++) {
            physicalLiveRanges[i] = new BitArraySet(func.getMaxOperNum() + 1);
        }

        for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                currBlock = currBlock.getNextBlock()) {

            // need BitArraySet of size=numPhysicalRegs;
            BitArraySet currentLiveness = new BitArraySet(availableRegs + 1);

            // walk backwards through opers
            for (Operation currOper = currBlock.getLastOper(); currOper != null;
                    currOper = currOper.getPrevOper()) {

                for (int i = 0; i < Operation.MAX_DEST_OPERANDS; i++) {
                    Operand currOperand = currOper.getDestOperand(i);
                    if ((currOperand != null) &&
                            (currOperand.getType() == Operand.OperandType.MACRO)) {
                        int regNum = getNumFromMacro(currOperand.getValue());
                        if (regNum > 0) {
                            currentLiveness.remove(regNum);
                        }
                    }
                }
                // need to turn off live ranges of caller save
                if (lastOperWasJSR) {
                    currentLiveness.remove(getNumFromMacro("EAX"));
                    currentLiveness.remove(getNumFromMacro("RDI"));
                    currentLiveness.remove(getNumFromMacro("RSI"));
                    currentLiveness.remove(getNumFromMacro("RDX"));
                    currentLiveness.remove(getNumFromMacro("RCX"));
                    currentLiveness.remove(getNumFromMacro("R8"));
                    currentLiveness.remove(getNumFromMacro("R9"));
                    currentLiveness.remove(getNumFromMacro("R10"));
                    currentLiveness.remove(getNumFromMacro("R11"));
                }

                // update for uses
                for (int i = 0; i < Operation.MAX_SRC_OPERANDS; i++) {
                    Operand currOperand = currOper.getSrcOperand(i);
                    if ((currOperand != null) &&
                            (currOperand.getType() == Operand.OperandType.MACRO)) {
                        int regNum = getNumFromMacro(currOperand.getValue());
                        currentLiveness.add(regNum);
                    }
                }

                // update caller save if JSR
                if (currOper.getType() == Operation.OperationType.CALL) {
                    currentLiveness.add(getNumFromMacro("R10"));
                    currentLiveness.add(getNumFromMacro("R11"));
                    lastOperWasJSR = true;

                    currentLiveness.add(getNumFromMacro("RDI"));
                    currentLiveness.add(getNumFromMacro("RSI"));
                    currentLiveness.add(getNumFromMacro("RDX"));
                    currentLiveness.add(getNumFromMacro("RCX"));
                    currentLiveness.add(getNumFromMacro("R8"));
                    currentLiveness.add(getNumFromMacro("R9"));
                    currentLiveness.add(getNumFromMacro("EAX"));

                }

                // now go through currentLiveness and update liveRanges
                for (int i = 0; i < availableRegs + 1; i++) {
                    if (currentLiveness.contains(i)) {
                        physicalLiveRanges[i].add(currOper.getNum());
                    }
                }
            }
        }
    }

    private int getNumFromMacro(Object macro) {
        // we are passed an Object (which should be a String).  We need to convert
        // it to an int physical reg num using following code:  Note caller save
        // used first
        //  1. EAX
        //  2. ECX
        //  3. EDX
        //  4. EBX
        //  5. EBP
        //  6 EDI
        //  7 ESI
        //  8 ESP

        // That was the order for x86
        // Here is order for x64
//        1.  RAX   (return reg)
//        2.  RDI   (parameter passing)
//        3.  RSI   (parameter passing)
//        4.  RDX   (parameter passing)
//        5.  RCX    (parameter passing)
//        6.  R8    (parameter passing)
//        7.  R9   (parameter passing)
//        8.  R10d    (caller save)
//        9.  R11d    (caller save
//        10. RBX   (callee save)
//        11. RBP   (callee save)
//        12. R12d   (callee save)
//        13. R13d   (callee save)
//        14. R14d   (callee save)
//        15. R15d   (callee save)
//        16. RSP (used for stack only)

        int returnVal;

        if (!(macro instanceof String)) {
            throw new X86CodegenException("RegAlloc: bad macro value");
        }

        String mac = (String) macro;

        if (mac.compareTo("EAX") == 0 || mac.compareTo("RAX") == 0) {
            returnVal = 1;
        }
        else if (mac.compareTo("ECX") == 0 || mac.compareTo("RCX") == 0) {
            returnVal = 5;
        }
        else if (mac.compareTo("EDX") == 0 || mac.compareTo("RDX") == 0) {
            returnVal = 4;
        }
        else if (mac.compareTo("EBX") == 0 || mac.compareTo("RBX") == 0) {
            returnVal = 10;
        }
        else if (mac.compareTo("EBP") == 0 || mac.compareTo("RBP") == 0) {
            returnVal = 11;
        }
        else if (mac.compareTo("EDI") == 0 || mac.compareTo("RDI") == 0) {
            returnVal = 2;
        }
        else if (mac.compareTo("ESI") == 0 || mac.compareTo("RSI") == 0) {
            returnVal = 3;
        }
        else if (mac.compareTo("ESP") == 0 || mac.compareTo("RSP") == 0) {
            returnVal = 16;
        }
        else if (mac.compareTo("R8") == 0 || mac.compareTo("R8D") == 0) {
            returnVal = 6;
        }
        else if (mac.compareTo("R9") == 0 || mac.compareTo("R9D") == 0) {
            returnVal = 7;
        }
        else if (mac.compareTo("R10") == 0 || mac.compareTo("R10D") == 0) {
            returnVal = 8;
        }
        else if (mac.compareTo("R11") == 0 || mac.compareTo("R11D") == 0) {
            returnVal = 9;
        }
        else if (mac.compareTo("R12") == 0 || mac.compareTo("R12D") == 0) {
            returnVal = 12;
        }
        else if (mac.compareTo("R13") == 0 || mac.compareTo("R13D") == 0) {
            returnVal = 13;
        }
        else if (mac.compareTo("R14") == 0 || mac.compareTo("R14D") == 0) {
            returnVal = 14;
        }
        else if (mac.compareTo("R15") == 0 || mac.compareTo("R15D") == 0) {
            returnVal = 15;
        }
        else {
            returnVal = 0;
        }
        return returnVal;
    }

    private String getMacroNameFromNum(int macroNum) {

        switch (macroNum) {
            case 1:
                return "EAX";
            case 2:
                return "EDI";
            case 3:
                return "ESI";
            case 4:
                return "EDX";
            case 5:
                return "ECX";
            case 6:
                return "R8D";
            case 7:
                return "R9D";
            case 8:
                return "R10D";
            case 9:
                return "R11D";
            case 10:
                return "EBX";
            case 11:
                return "EBP";
            case 12:
                return "R12D";
            case 13:
                return "R13D";
            case 14:
                return "R14D";
            case 15:
                return "R15D";
            case 16:
                return "ESP";
            default:
                throw new X86CodegenException("RegAlloc: bad macro num");
        }

    }

    private void assignPhysicalRegs(Function func) {
        // we need to assign a physical reg to each liveRange
        // we pull virtual regs off the front of allocated list, because the ones
        // in back are less constrained

        regMap = new int[func.getMaxRegNum() + 1];
        usedRegs = new boolean[availableRegs + 1];

        Iterator listIter = allocatedRegs.iterator();

        while (listIter.hasNext()) {
            int currReg = ((Integer) listIter.next()).intValue();
            boolean foundReg = false;

            boolean regSpansCall = doesRegSpanFuncCall(func, currReg);


            if (regSpansCall) {
                // put in callee save first
                // since we don't handle caller save now, throw exception if can't put
                // in callee save
                // Note: later versions could easily remove this restriction by supporting
                // use of caller save

                final int FIRST_CALLEE = 10;

                for (int i = availableRegs; i >= FIRST_CALLEE; i--) {
                    if (liveRanges[currReg].intersect(physicalLiveRanges[i]).isEmpty()) {
                        regMap[currReg] = i;
                        usedRegs[i] = true;
                        physicalLiveRanges[i] =
                                physicalLiveRanges[i].union(liveRanges[currReg]);
                        foundReg = true;
                        break;
                    }
                }
                if (!foundReg) {
                    throw new X86CodegenException("RegAlloc: unable to allocate callee-save reg");
                }
            }
            else {
                // for regs not spanning a JSR, we prefer caller-save, which are the first 9
                // if have to use callee-save, that's fine
                for (int i = 1; i <= availableRegs; i++) {
                    // for each physical reg, we see in live range conflicts
                    // if not, we have a match
                    if (liveRanges[currReg].intersect(physicalLiveRanges[i]).isEmpty()) {
                        regMap[currReg] = i;
                        usedRegs[i] = true;
                        physicalLiveRanges[i] =
                                physicalLiveRanges[i].union(liveRanges[currReg]);
                        foundReg = true;
                        break;
                    }
                }
                if (!foundReg) {
                    throw new X86CodegenException("RegAlloc: unable to match physical reg");
                }
            }
        }
    }

    private boolean doesRegSpanFuncCall(Function func, int regNum) {
        // simply search for every JSR, and see if reg is in it's use set

        boolean foundSpan = false;

        for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                currBlock = currBlock.getNextBlock()) {

            for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                    currOper = currOper.getNextOper()) {
                if (currOper.getType() == Operation.OperationType.CALL) {
                    if (liveRanges[regNum].contains(currOper.getNum())) {
                        return true;
                    }
                }
            }
        }
        return foundSpan;
    }

    private void annotateRegisters(Function func) {
        for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                currBlock = currBlock.getNextBlock()) {

            for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                    currOper = currOper.getNextOper()) {

                for (int i = 0; i < Operation.MAX_DEST_OPERANDS; i++) {
                    Operand currOperand = currOper.getDestOperand(i);
                    if ((currOperand != null) &&
                            (currOperand.getType() == Operand.OperandType.REGISTER)) {
                        int regNum = ((Integer) currOperand.getValue()).intValue();
                        if (regMap[regNum] > 0) {
                            currOper.setDestOperand(i, new Operand(Operand.OperandType.MACRO,
                                    getMacroNameFromNum(regMap[regNum])));
                        }
                    }
                }

                // update for uses
                for (int i = 0; i < Operation.MAX_SRC_OPERANDS; i++) {
                    Operand currOperand = currOper.getSrcOperand(i);
                    if ((currOperand != null) &&
                            (currOperand.getType() == Operand.OperandType.REGISTER)) {
                        int regNum = ((Integer) currOperand.getValue()).intValue();
                        if (regMap[regNum] > 0) {
                            currOper.setSrcOperand(i, new Operand(Operand.OperandType.MACRO,
                                    getMacroNameFromNum(regMap[regNum])));
                        }
                    }
                }
            }
        }
    }

    private void updateForCalleeSave(Function func) {
        // 3 things to do:
        // 1. insert pushes in block0
        // 2. update offsets to incoming params in block0
        // 3. insert pops in returnblock
        for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                currBlock = currBlock.getNextBlock()) {
            // if block 0
            if (currBlock.getPrevBlock() == null) {
                // stores how much param offsets needed to be adjusted because of
                // callee saves
                int fudgeFactor = 0;

                // the 1st oper should be the push %ebp
//        Operation insertOper =
//            currBlock.getFirstOper();
//
//        if ( (insertOper == null) ||
//            (insertOper.getType() != Operation.OperationType.X86_PUSH) ||
//            (insertOper.getSrcOperand(0) == null) ||
//            ( ( (String) insertOper.getSrcOperand(0).getValue()).compareTo(
//            "EBP") != 0)) {
//          throw new X86CodegenException("regalloc: callee save confused");
//        }
//        // if framesize > 0, then will have subl framesize, %ESP
//        if (func.getFrameSize() > 0) {
//          insertOper = insertOper.getNextOper();
//          if ( (insertOper == null) ||
//              (insertOper.getType() != Operation.OperationType.SUB_I) ||
//              (insertOper.getDestOperand(0) == null) ||
//              ( ( (String) insertOper.getDestOperand(0).getValue()).compareTo(
//              "ESP") != 0)) {
//            throw new X86CodegenException("regalloc: callee save confused");
//          }
//        }
                Operation subsequentOper = currBlock.getFirstOper();

                if (usedRegs[15]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_PUSH, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(15)));
                    newOper.setSrcOperand(0, src);
                    currBlock.insertFirst(newOper);
                    fudgeFactor += 4;
                }
                if (usedRegs[14]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_PUSH, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(14)));
                    newOper.setSrcOperand(0, src);
                    currBlock.insertFirst(newOper);
                    fudgeFactor += 4;
                }
                if (usedRegs[13]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_PUSH, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(13)));
                    newOper.setSrcOperand(0, src);
                    currBlock.insertFirst(newOper);
                    fudgeFactor += 4;
                }
                if (usedRegs[12]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_PUSH, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(12)));
                    newOper.setSrcOperand(0, src);
                    currBlock.insertFirst(newOper);
                    fudgeFactor += 4;
                }
                if (usedRegs[11]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_PUSH, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(11)));
                    newOper.setSrcOperand(0, src);
                    currBlock.insertFirst(newOper);
                    fudgeFactor += 4;
                }
                if (usedRegs[10]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_PUSH, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(10)));
                    newOper.setSrcOperand(0, src);
                    currBlock.insertFirst(newOper);
                    fudgeFactor += 4;
                }
                // now time to search rest of block 0 for handling income params
                // need to adjust by fudgeFactor
                if (fudgeFactor > 0) {
                    for (Operation currOper = subsequentOper; currOper != null;
                            currOper = currOper.getNextOper()) {
                        // match pattern: load oper, reg dest, macro esp src0, int src1
                        if (currOper.getType() != Operation.OperationType.LOAD_I) {
                            continue;
                        }
                        if (currOper.getDestOperand(0) == null) {
                            continue;
                        }
                        if (currOper.getDestOperand(0).getType() != Operand.OperandType.MACRO) {
                            continue;
                        }
                        if (currOper.getSrcOperand(1) == null) {
                            continue;
                        }
                        if (currOper.getSrcOperand(1).getType() != Operand.OperandType.INTEGER) {
                            continue;
                        }
                        if (currOper.getSrcOperand(0) == null) {
                            continue;
                        }
                        if (currOper.getSrcOperand(0).getType() != Operand.OperandType.MACRO) {
                            continue;
                        }
                        if (((String) currOper.getSrcOperand(0).getValue()).compareTo("ESP") != 0) {
                            continue;
                        }
                        int oldOffset =
                                ((Integer) currOper.getSrcOperand(1).getValue()).intValue();
                        currOper.getSrcOperand(1).setValue(new Integer(oldOffset + fudgeFactor));
                    }
                }
            }
            // else if return block
            else if (currBlock.getLastOper() != null &&
                    currBlock.getLastOper().getType() == Operation.OperationType.RETURN) {
                // we insert after the addl ESP and before the RET
                Operation insertOper = currBlock.getLastOper();
                if (usedRegs[10]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_POP, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(10)));
                    newOper.setDestOperand(0, src);
                    currBlock.insertOperBefore(insertOper, newOper);
                    insertOper = newOper;
                }
                if (usedRegs[11]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_POP, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(11)));
                    newOper.setDestOperand(0, src);
                    currBlock.insertOperBefore(insertOper, newOper);
                    insertOper = newOper;
                }
                if (usedRegs[12]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_POP, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(12)));
                    newOper.setDestOperand(0, src);
                    currBlock.insertOperBefore(insertOper, newOper);
                    insertOper = newOper;
                }
                if (usedRegs[13]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_POP, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(13)));
                    newOper.setDestOperand(0, src);
                    currBlock.insertOperBefore(insertOper, newOper);
                    insertOper = newOper;
                }
                if (usedRegs[14]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_POP, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(14)));
                    newOper.setDestOperand(0, src);
                    currBlock.insertOperBefore(insertOper, newOper);
                    insertOper = newOper;
                }
                if (usedRegs[15]) {
                    Operation newOper = new Operation(Operation.OperationType.X86_POP, currBlock);
                    Operand src = new Operand(Operand.OperandType.MACRO,
                            new String(getMacroNameFromNum(15)));
                    newOper.setDestOperand(0, src);
                    currBlock.insertOperBefore(insertOper, newOper);
                    insertOper = newOper;
                }
            }
        }
    }

    private void annotateSpills(Function func) {

        if (!spilledRegs.isEmpty()) {
            throw new X86CodegenException("RegAlloc: we don't handle spills yet");
        }

    }

    private void checkForUnallocatedRegs(Function func) {
        for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                currBlock = currBlock.getNextBlock()) {
            for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                    currOper = currOper.getNextOper()) {

                for (int i = 0; i < Operation.MAX_DEST_OPERANDS; i++) {
                    Operand currOperand = currOper.getDestOperand(i);
                    if ((currOperand != null) &&
                            (currOperand.getType() == Operand.OperandType.REGISTER)) {
                        throw new X86CodegenException("RegAlloc: unconverted reg(1) " +
                                "in oper " + currOper.getNum());
                    }
                }

                // update for uses
                for (int i = 0; i < Operation.MAX_SRC_OPERANDS; i++) {
                    Operand currOperand = currOper.getSrcOperand(i);
                    if ((currOperand != null) &&
                            (currOperand.getType() == Operand.OperandType.REGISTER)) {
                        throw new X86CodegenException("RegAlloc: unconverted reg(2) " +
                                "in oper " + currOper.getNum());
                    }
                }
            }
        }
    }
}
