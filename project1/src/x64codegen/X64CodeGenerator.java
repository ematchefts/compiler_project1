package x64codegen;

import lowlevel.*;

public class X64CodeGenerator {
    // since using EBP as general reg, no push of EBP and offset only 4

    private static final int FIRST_PARAM_OFFSET = 4;
    private CodeItem firstItem;

    public X64CodeGenerator(CodeItem first) {
        firstItem = first;
    }

    public void convertToX64() {
        x64ConvertFuncEntryAndExit();
        x64ConvertReturnReg();
        x64ConvertOperations();
    }

    private void x64ConvertFuncEntryAndExit() {

        boolean foundEntry = false;
        boolean foundExit = false;
        for (CodeItem currItem = firstItem; currItem != null;
                currItem = currItem.getNextItem()) {
            if (currItem instanceof Data) {
                continue;
            }
            Function func = (Function) currItem;
            for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                    currBlock = currBlock.getNextBlock()) {
                if ((currBlock.getFirstOper() != null) &&
                        (currBlock.getFirstOper().getType() == Operation.OperationType.FUNC_ENTRY)) {
                    x64ConvertFuncEntry(currBlock);
                    foundEntry = true;
                }
                else if ((currBlock.getFirstOper() != null) &&
                        (currBlock.getFirstOper().getType() ==
                        Operation.OperationType.FUNC_EXIT)) {
                    x64ConvertFuncExit(currBlock);
                    foundExit = true;
                }
            }
            if (!foundEntry) {
                throw new X64CodegenException("convertFuncEntryExit: entry not found");
            }
            if (!foundExit) {
                throw new X64CodegenException("convertFuncEntryExit: exit not found");
            }
        }
    }

    private void x64ConvertFuncEntry(BasicBlock block) {

        int frameSize = block.getFunc().getFrameSize();
        // remove Func_Entry oper
        block.removeOper(block.getFirstOper());
        // no longer use EBP, so first entry insts not needed

        // for x64, use quad version of subtract RSP
        Operation currOper = block.getFirstOper();

        if (frameSize > 0) {
            Operation oper3 = new Operation(Operation.OperationType.X64_SUB_Q, block);
            Operand src0 = new Operand(Operand.OperandType.MACRO, "RSP");
            oper3.setSrcOperand(0, src0);
            Operand src1 = new Operand(Operand.OperandType.INTEGER, new Integer(frameSize));
            oper3.setSrcOperand(1, src1);
            Operand dest0 = new Operand(Operand.OperandType.MACRO, "RSP");
            oper3.setDestOperand(0, dest0);
            if (block.getFirstOper() == null) {
                block.appendOper(oper3);
            }
            else {
                block.insertOperBefore(block.getFirstOper(), oper3);
            }

        }
        // now move params from memory into register
        // for x64, assume 1st 6 params in register already
        //      so ASSIGN from macro into general reg

        Function func = block.getFunc();
        int paramNum = 0;
        for (FuncParam currParam = func.getfirstParam(); currParam != null;
                currParam = currParam.getNextParam()) {
            String name = currParam.getName();
            int regNum = ((Integer) func.getTable().get(name)).intValue();
            Operation assignOper = new Operation(Operation.OperationType.ASSIGN, block);
            String passRegName = getX64PassRegNameFromNum(paramNum);
            Operand src0 = new Operand(Operand.OperandType.MACRO, passRegName);
            assignOper.setSrcOperand(0, src0);
            Operand dest0 = new Operand(Operand.OperandType.REGISTER, new Integer(regNum));
            assignOper.setDestOperand(0, dest0);
            if (currOper == null) {
                block.insertFirst(assignOper);
            }
            else {
                block.insertOperAfter(currOper, assignOper);
            }
            currOper = assignOper;
            paramNum++;
        }
    }

    private String getX64PassRegNameFromNum(int passNum) {
        switch (passNum) {
            case 0:
                return "EDI";
            case 1:
                return "ESI";
            case 2:
                return "EDX";
            case 3:
                return "ECX";
            case 4:
                return "R8D";
            case 5:
                return "R9D";
            default:
                throw new X64CodegenException("getX64PassRegNameFromNum: unexpected passNum");
        }
    }

    private void x64ConvertFuncExit(BasicBlock block) {
        int frameSize = block.getFunc().getFrameSize();
        // remove Func_Exit oper
        block.removeOper(block.getFirstOper());
        // ESP=EBP   Pop EBP

        if (block.getFirstOper() == null) {
            throw new X64CodegenException("funcExit: no return oper found");
        }

        Operation oper3 = null;
        if (frameSize > 0) {
            oper3 = new Operation(Operation.OperationType.X64_ADD_Q, block);
            Operand src0 = new Operand(Operand.OperandType.MACRO, "RSP");
            oper3.setSrcOperand(0, src0);
            Operand src1 = new Operand(Operand.OperandType.INTEGER, new Integer(frameSize));
            oper3.setSrcOperand(1, src1);
            Operand dest0 = new Operand(Operand.OperandType.MACRO, "RSP");
            oper3.setDestOperand(0, dest0);
            block.insertOperBefore(block.getFirstOper(), oper3);
        }

    }

    private void x64ConvertReturnReg() {
        for (CodeItem currItem = firstItem; currItem != null;
                currItem = currItem.getNextItem()) {
            if (currItem instanceof Data) {
                continue;
            }
            Function func = (Function) currItem;
            for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                    currBlock = currBlock.getNextBlock()) {
                for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                        currOper = currOper.getNextOper()) {

                    for (int i = 0; i < Operation.MAX_DEST_OPERANDS; i++) {
                        Operand currOperand = currOper.getDestOperand(i);
                        if ((currOperand != null) &&
                                (currOperand.getType() == Operand.OperandType.MACRO)) {
                            if (((String) currOperand.getValue()).compareTo("RetReg") == 0) {
                                currOperand.setValue(new String("EAX"));
                            }
                        }
                    }

                    // update for uses
                    for (int i = 0; i < Operation.MAX_SRC_OPERANDS; i++) {
                        Operand currOperand = currOper.getSrcOperand(i);
                        if ((currOperand != null) &&
                                (currOperand.getType() == Operand.OperandType.MACRO)) {
                            if (((String) currOperand.getValue()).compareTo("RetReg") == 0) {
                                currOperand.setValue(new String("EAX"));
                            }
                        }
                    }
                }
            }
        }
    }

    private void x64ConvertOperations() {
        for (CodeItem currItem = firstItem; currItem != null;
                currItem = currItem.getNextItem()) {
            if (currItem instanceof Data) {
                continue;
            }
            Function func = (Function) currItem;
            for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
                    currBlock = currBlock.getNextBlock()) {
                for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                        currOper = currOper.getNextOper()) {
                    switch (currOper.getType()) {
                        case ASSIGN:
                            x64ConvertAssignOper(currOper);
                            break;
                        case LT:
                        case LTE:
                        case GT:
                        case GTE:
                        case EQUAL:
                        case NOT_EQUAL:
                            x64ConvertComparisonOper(currOper);
                            break;
                        case BEQ:
                        case BNE:
                            x64ConvertBranchOper(currOper);
                            break;
                        case PASS:
                            x64ConvertPassOper(currOper);
                            break;
                        case MUL_I:
                            x64ConvertMulOper(currOper);
                            break;
                        case DIV_I:
                            x64ConvertDivOper(currOper);
                            break;
                        case ADD_I:
                            x64ConvertAddOper(currOper);
                            break;
                        case SUB_I:
                            x64ConvertSubOper(currOper);
                            break;
                        case CALL:
                            x64ConvertCallOper(currOper);
                            break;
                        case UNKNOWN:
                        case FUNC_ENTRY:
                        case FUNC_EXIT:
                        case RETURN:
                        case JMP:
                        case LOAD_I:
                        case STORE_I:
                        case X86_PUSH:
                        case X86_POP:
                        case X86_MOV:
                        case X86_CMP:
                        case X86_BEQ:
                        case X86_BNE:
                        case X86_BLT:
                        case X86_BLE:
                        case X86_BGT:
                        case X86_BGE:
                        case X86_MUL_I:
                        case X86_DIV_I:
                        case X64_ADD_Q:
                        case X64_SUB_Q:
                        case X64_LOAD_Q:
                        case X64_STORE_Q:
                            break;
                        default:
                            throw new X64CodegenException("convertOper: unexpected op type");
                    }
                }
            }
        }
    }

    public void x64ConvertAssignOper(Operation oper) {
        // Simply change to a MOV oper
        oper.setType(Operation.OperationType.X86_MOV);
    }

    // note: no changes made for x64
    public void x64ConvertComparisonOper(Operation oper) {
        // there are 2 cases we must handle: the common case where the compare is
        // part of a branch, and the less common where the compare is standalone, e.g.,
        // a = b < c
        // here we convert the comparison to a CMP
        // and then convert the subsequent branch to the correct type
        BasicBlock block = oper.getBlock();
        Operation.OperationType type = oper.getType();
        Operation cmp = new Operation(Operation.OperationType.X86_CMP, block);

        if (oper.getSrcOperand(0).getType() == Operand.OperandType.INTEGER) {
            Operation mov = new Operation(Operation.OperationType.X86_MOV, block);
            Operand src = new Operand(oper.getSrcOperand(0));
            mov.setSrcOperand(0, src);
            int regNum = block.getFunc().getNewRegNum();
            Operand dest = new Operand(Operand.OperandType.REGISTER, new Integer(regNum));
            mov.setDestOperand(0, dest);
            block.insertOperBefore(oper, mov);

            cmp.setSrcOperand(0, new Operand(mov.getDestOperand(0)));
        }
        else {
            cmp.setSrcOperand(0, new Operand(oper.getSrcOperand(0)));
        }

        cmp.setSrcOperand(1, new Operand(oper.getSrcOperand(1)));
        Operand dest = new Operand(Operand.OperandType.MACRO, "Flags");
        cmp.setDestOperand(0, dest);

        block.insertOperAfter(oper, cmp);
        block.removeOper(oper);

        // the next operation is likely a branch; if not, generate a sequence for
        // dest = Src0 < src1
        Operation oldBranch = cmp.getNextOper();
        if (oldBranch == null ||
                (oldBranch.getType() != Operation.OperationType.BEQ &&
                oldBranch.getType() != Operation.OperationType.BNE)) {
            // first dest = 0
            Operation newOper = new Operation(Operation.OperationType.X86_MOV, block);
            newOper.setDestOperand(0, new Operand(oper.getDestOperand(0)));
            newOper.setSrcOperand(0, new Operand(Operand.OperandType.INTEGER, new Integer(0)));
            block.insertOperBefore(cmp, newOper);
            // have  dest=0; flags=cmp; now need jump
            // first need then and post blocks
            Function func = block.getFunc();
            BasicBlock then = new BasicBlock(func);
            func.insertBlockAfter(block, then);
            BasicBlock post = new BasicBlock(func);
            func.insertBlockAfter(then, post);
            Operation newBranch = new Operation(x64GetReverseBranchTypeFromCmp(type),
                    block);

            Operand flags = new Operand(Operand.OperandType.MACRO, "Flags");
            newBranch.setSrcOperand(0,
                    new Operand(Operand.OperandType.BLOCK,
                    new Integer(post.getBlockNum())));
            newBranch.setSrcOperand(1, flags);
            block.insertOperAfter(cmp, newBranch);
            Operation setZero = new Operation(Operation.OperationType.X86_MOV, then);
            setZero.setDestOperand(0, new Operand(oper.getDestOperand(0)));
            setZero.setSrcOperand(0, new Operand(Operand.OperandType.INTEGER, new Integer(1)));
            then.appendOper(setZero);
            // now, need to move any operations from after then compare into the post block
            Operation next;
            for (Operation curr = oldBranch; curr != null; curr = next) {
                // splice out curr
                Operation prev = curr.getPrevOper();
                if (prev == null) {
                    block.setFirstOper(curr.getNextOper());
                }
                else {
                    prev.setNextOper(curr.getNextOper());
                }
                next = curr.getNextOper();
                if (next == null) {
                    block.setLastOper(prev);
                }
                else {
                    next.setPrevOper(curr.getPrevOper());
                }
                curr.setPrevOper(null);
                curr.setNextOper(null);
                post.appendOper(curr);
            }
        }
        else {
            Operation newBranch;

            if (oldBranch.getType() == Operation.OperationType.BEQ) {
                newBranch = new Operation(x64GetReverseBranchTypeFromCmp(type), block);
            }
            else if (oldBranch.getType() == Operation.OperationType.BNE) {
                newBranch = new Operation(x64GetBranchTypeFromCmp(type), block);
            }
            else {
                throw new X64CodegenException("convertCompare: next op not branch");
            }
            Operand flags = new Operand(Operand.OperandType.MACRO, "Flags");
            newBranch.setSrcOperand(0, oldBranch.getSrcOperand(2));
            newBranch.setSrcOperand(1, flags);
            block.insertOperAfter(cmp, newBranch);
            block.removeOper(oldBranch);
        }
    }

    public void x64ConvertBranchOper(Operation oper) {
        // Most branches will have been converted to x86 form when the
        // comparison was discovered.  However, some don't have comparison
        // operations associated with them, and we fix them here.
        // We also need to be sure operands are correct

        // We first generate the correct CMP oper, then convert the branch
        BasicBlock block = oper.getBlock();

        Operation cmp = new Operation(Operation.OperationType.X86_CMP, block);
        if (oper.getSrcOperand(0).getType() == Operand.OperandType.INTEGER) {
            Operation mov = new Operation(Operation.OperationType.X86_MOV, block);
            Operand src = new Operand(oper.getSrcOperand(0));
            mov.setSrcOperand(0, src);
            int regNum = block.getFunc().getNewRegNum();
            Operand dest = new Operand(Operand.OperandType.REGISTER, new Integer(regNum));
            mov.setDestOperand(0, dest);
            block.insertOperBefore(oper, mov);

            cmp.setSrcOperand(0, new Operand(mov.getDestOperand(0)));
        }
        else {
            cmp.setSrcOperand(0, new Operand(oper.getSrcOperand(0)));
        }
        cmp.setSrcOperand(1, new Operand(oper.getSrcOperand(1)));
        Operand dest = new Operand(Operand.OperandType.MACRO, "Flags");
        cmp.setDestOperand(0, dest);
        block.insertOperBefore(oper, cmp);

        // finally we convert branch to a x86 version
        if (oper.getType() == Operation.OperationType.BEQ) {
            oper.setType(Operation.OperationType.X86_BEQ);
        }
        else if (oper.getType() == Operation.OperationType.BNE) {
            oper.setType(Operation.OperationType.X86_BNE);
        }
        oper.setSrcOperand(0, oper.getSrcOperand(2));
        oper.setSrcOperand(1, new Operand(Operand.OperandType.MACRO, "Flags"));
        oper.setSrcOperand(2, null);
    }

    public Operation.OperationType x64GetBranchTypeFromCmp(Operation.OperationType type) {
        switch (type) {
            case LT:
                return Operation.OperationType.X86_BLT;
            case LTE:
                return Operation.OperationType.X86_BLE;
            case GT:
                return Operation.OperationType.X86_BGT;
            case GTE:
                return Operation.OperationType.X86_BGE;
            case EQUAL:
                return Operation.OperationType.X86_BEQ;
            case NOT_EQUAL:
                return Operation.OperationType.X86_BNE;
            default:
                throw new X64CodegenException("BranchTypeFromCmp: unexpected type");
        }
    }

    public Operation.OperationType x64GetReverseBranchTypeFromCmp(Operation.OperationType type) {
        switch (type) {
            case LT:
                return Operation.OperationType.X86_BGE;
            case LTE:
                return Operation.OperationType.X86_BGT;
            case GT:
                return Operation.OperationType.X86_BLE;
            case GTE:
                return Operation.OperationType.X86_BLT;
            case EQUAL:
                return Operation.OperationType.X86_BNE;
            case NOT_EQUAL:
                return Operation.OperationType.X86_BEQ;
            default:
                throw new X64CodegenException("BranchTypeFromCmp: unexpected type");
        }
    }

    public void x64ConvertPassOper(Operation oper) {
        // now pass by register for 1st 6 regs
        // oper must have attribute PARAM_NUM (0-5) to indicate which it is
        int parmNum = -1;
        Attribute currAttr = oper.getAttribute();
        while (currAttr != null) {
            if (currAttr.getName().equals("PARAM_NUM")) {
                parmNum = Integer.parseInt(currAttr.getValue());
                break;
            }
        }
        if (parmNum < 0) {
            throw new X64CodegenException("x64ConvertPassOper: no PARAM_NUM attr");
        }
        if (parmNum < 6) {
            // need move into MACRO reg from pass reg
            BasicBlock currBlock = oper.getBlock();
            Operation mov = new Operation(Operation.OperationType.X86_MOV, currBlock);
            Operand src = new Operand(oper.getSrcOperand(0));
            mov.setSrcOperand(0, src);
            String macroName = getX64PassRegNameFromNum(parmNum);
            Operand dest = new Operand(Operand.OperandType.MACRO, new String(macroName));
            mov.setDestOperand(0, dest);
            currBlock.insertOperBefore(oper, mov);
            currBlock.removeOper(oper);
        }
        else {
            // still need push
            oper.setType(Operation.OperationType.X86_PUSH);
        }
    }

    public void x64ConvertMulOper(Operation oper) {
        // The multiply uses particular regs.  In particular, the src0 must be
        // EAX and the dest must be EAX/EDX
        // Also, the src1 must be a reg
        BasicBlock block = oper.getBlock();
        // x86 muls have different form, so change type
        oper.setType(Operation.OperationType.X86_MUL_I);

        Operation preMov1 = new Operation(Operation.OperationType.X86_MOV, block);
        preMov1.setSrcOperand(0, oper.getSrcOperand(0));
        preMov1.setDestOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        block.insertOperBefore(oper, preMov1);
        // src1 must be a reg, so add mov if necessary
        if (oper.getSrcOperand(1).getType() == Operand.OperandType.INTEGER) {
            Operation preMov2 = new Operation(Operation.OperationType.X86_MOV, block);
            preMov2.setSrcOperand(0, oper.getSrcOperand(1));
            int regNum = block.getFunc().getNewRegNum();
            preMov2.setDestOperand(0,
                    new Operand(Operand.OperandType.REGISTER,
                    new Integer(regNum)));
            block.insertOperBefore(oper, preMov2);
            oper.setSrcOperand(1, new Operand(preMov2.getDestOperand(0)));
        }
        oper.setSrcOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        // now move EAX to original dest reg
        Operation postMov = new Operation(Operation.OperationType.X86_MOV, block);
        postMov.setDestOperand(0, oper.getDestOperand(0));
        postMov.setSrcOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        block.insertOperAfter(oper, postMov);
        oper.setDestOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        oper.setDestOperand(1, new Operand(Operand.OperandType.MACRO, "EDX"));
    }

    public void x64ConvertDivOper(Operation oper) {
        // x86 divides take the EAX:EDX combo as source
        // The quotient goes to EAX and the REM goes to EDX
        // Since we deal with 32 bit nums only, we will zero out EDX first

        BasicBlock block = oper.getBlock();
        oper.setType(Operation.OperationType.X86_DIV_I);
        // first, zero EDX
        Operation zero = new Operation(Operation.OperationType.X86_MOV, block);
        zero.setSrcOperand(0, new Operand(Operand.OperandType.INTEGER, new Integer(0)));
        zero.setDestOperand(0, new Operand(Operand.OperandType.MACRO, "EDX"));
        block.insertOperBefore(oper, zero);
        // again, src0 must be EAX
        Operation preMov1 = new Operation(Operation.OperationType.X86_MOV, block);
        preMov1.setSrcOperand(0, oper.getSrcOperand(0));
        preMov1.setDestOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        block.insertOperBefore(oper, preMov1);
        // src1 must be a reg, so add mov if necessary
        if (oper.getSrcOperand(1).getType() == Operand.OperandType.INTEGER) {
            Operation preMov2 = new Operation(Operation.OperationType.X86_MOV, block);
            preMov2.setSrcOperand(0, oper.getSrcOperand(1));
            int regNum = block.getFunc().getNewRegNum();
            preMov2.setDestOperand(0,
                    new Operand(Operand.OperandType.REGISTER,
                    new Integer(regNum)));
            block.insertOperBefore(oper, preMov2);
            oper.setSrcOperand(1, new Operand(preMov2.getDestOperand(0)));
        }
        oper.setSrcOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        // must show EDX a source also, in src2
        oper.setSrcOperand(2, new Operand(Operand.OperandType.MACRO, "EDX"));
        // now move EAX to original dest reg
        Operation postMov = new Operation(Operation.OperationType.X86_MOV, block);
        postMov.setDestOperand(0, oper.getDestOperand(0));
        postMov.setSrcOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        block.insertOperAfter(oper, postMov);
        oper.setDestOperand(0, new Operand(Operand.OperandType.MACRO, "EAX"));
        oper.setDestOperand(1, new Operand(Operand.OperandType.MACRO, "EDX"));
    }

    public void x64ConvertAddOper(Operation oper) {
        // x86 adds must have dest and src0 the same.  Src1 can be either reg
        // or immediate.  If src1 matches dest, then we can swap operands
        boolean fixed = false;

        BasicBlock block = oper.getBlock();

        Operand dest = oper.getDestOperand(0);

        if (dest == null) {
            throw new X64CodegenException("convertSub: dest is null");
        }
        if (dest.getType() == Operand.OperandType.REGISTER) {
            int destReg = ((Integer) oper.getDestOperand(0).getValue()).intValue();
            // if dest == src0, no mod required
            if (oper.getSrcOperand(0).getType() == Operand.OperandType.REGISTER) {
                if (destReg == ((Integer) oper.getSrcOperand(0).getValue()).intValue()) {
                    return;
                }
            }
            // if src1 == dest, then swap src0 and src1
            if ((oper.getSrcOperand(1).getType() == Operand.OperandType.REGISTER) &&
                    (destReg == ((Integer) oper.getSrcOperand(1).getValue()).intValue())) {
                Operand temp = oper.getSrcOperand(0);
                oper.setSrcOperand(0, oper.getSrcOperand(1));
                oper.setSrcOperand(1, temp);
                return;
            }
        }
        else if (dest.getType() == Operand.OperandType.MACRO) {
            String destMacro = (String) oper.getDestOperand(0).getValue();
            // if dest == src0, no mod required
            if (oper.getSrcOperand(0).getType() == Operand.OperandType.MACRO) {
                if (destMacro.equals((String) oper.getSrcOperand(0).getValue())) {
                    return;
                }
            }
            // if src1 == dest, then swap src0 and src1
            if ((oper.getSrcOperand(1).getType() == Operand.OperandType.MACRO) &&
                    (destMacro.equals((String) oper.getSrcOperand(1).getValue()))) {
                Operand temp = oper.getSrcOperand(0);
                oper.setSrcOperand(0, oper.getSrcOperand(1));
                oper.setSrcOperand(1, temp);
                return;
            }
        }
        else {
            throw new X64CodegenException("convertSub: unexpected dest type");
        }

        // otherwise, we have a problem that requires a mov to be inserted
        // R1 = R2 + R3  =>  R1 = R2; R1 = R1 + R3
        Operation preMov1 = new Operation(Operation.OperationType.X86_MOV, block);
        preMov1.setSrcOperand(0, oper.getSrcOperand(0));
        int regNum = block.getFunc().getNewRegNum();
        preMov1.setDestOperand(0, new Operand(oper.getDestOperand(0)));
        block.insertOperBefore(oper, preMov1);
        // now fix src0
        oper.setSrcOperand(0, new Operand(oper.getDestOperand(0)));
    }

    public void x64ConvertSubOper(Operation oper) {
        // x86 subs must have dest and src0 the same.  Src1 can be either reg
        // or immediate.  Unlike adds, we can't swap src0 and src1

        BasicBlock block = oper.getBlock();

        Operand dest = oper.getDestOperand(0);
        if (dest == null) {
            throw new X64CodegenException("convertSub: dest is null");
        }
        if (dest.getType() == Operand.OperandType.REGISTER) {
            int destReg = ((Integer) oper.getDestOperand(0).getValue()).intValue();
            // if dest == src0, no mod required
            if (oper.getSrcOperand(0).getType() == Operand.OperandType.REGISTER) {
                if (destReg == ((Integer) oper.getSrcOperand(0).getValue()).intValue()) {
                    return;
                }
            }
        }
        else if (dest.getType() == Operand.OperandType.MACRO) {
            String destMacro = (String) oper.getDestOperand(0).getValue();
            // if dest == src0, no mod required
            if (oper.getSrcOperand(0).getType() == Operand.OperandType.MACRO) {
                if (destMacro.equals((String) oper.getSrcOperand(0).getValue())) {
                    return;
                }
            }
        }
        else {
            throw new X64CodegenException("convertSub: unexpected dest type");
        }
        // we have a problem that requires a mov to be inserted
        // R1 = R2 - R3  =>  R1 = R2; R1 = R1 - R3

        Operation preMov1 = new Operation(Operation.OperationType.X86_MOV, block);
        preMov1.setSrcOperand(0, oper.getSrcOperand(0));
        int regNum = block.getFunc().getNewRegNum();
        preMov1.setDestOperand(0, new Operand(oper.getDestOperand(0)));
        block.insertOperBefore(oper, preMov1);
        // now fix src0
        oper.setSrcOperand(0, new Operand(oper.getDestOperand(0)));
    }

    private void x64ConvertCallOper(Operation currOper) {
        // here we need to insert the add oper to move the SP back into place
        // the number of parametes is in the Attribute of the oper
        String attrValue = currOper.findAttribute("numParams");
        if (attrValue == null) {
            throw new X64CodegenException("convertCall: no numParams attr found");
        }
        int numParams = Integer.parseInt(attrValue);
        // if less than 6 params, all through reg and no add required
        if (numParams > 6) {
            numParams -=6;
            int offset = numParams << 2;
            if (numParams > 0) {
                BasicBlock currBlock = currOper.getBlock();
                Operation newOper =
                        new Operation(Operation.OperationType.X64_ADD_Q, currBlock);
                Operand src0 = new Operand(Operand.OperandType.MACRO, "RSP");
                newOper.setSrcOperand(0, src0);
                Operand src1 = new Operand(Operand.OperandType.INTEGER, new Integer(offset));
                newOper.setSrcOperand(1, src1);
                Operand dest0 = new Operand(Operand.OperandType.MACRO, "RSP");
                newOper.setDestOperand(0, dest0);
                currBlock.insertOperAfter(currOper, newOper);

            }
        }
    }
}
