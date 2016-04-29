package lowlevel;

import java.util.*;
import java.io.*;
import dataflow.BitArraySet;

/**
 * This class is the primary low-level abstraction for a function
 *
 * @author Dr. Gallagher
 * @version 1.0
 * Created: 22 Apr 03
 * Summary of Modifications:
 *
 * Description:  Compilation typically is centered around the function.  This
 * class provides the primary abstraction for a function.  It extends CodeItem,
 * which provides a linked structure for chaining Data and Functions.
 */

public class Function extends CodeItem {
    // instance variables

    // the function return type (INT or VOID)
  private int funcType;
  private String funcName;
    // Reference to the first FuncParam in the list of FuncParams
  private FuncParam firstParam;
    // A function consists of a linked list of BasicBlocks.  The next 2
    // variables are the head/tail references for this list
  private BasicBlock firstBlock;
  private BasicBlock lastBlock;
    // The next 3 variables are for future use
  private int localVarSize;
  private int spillSize;
  private int frameSize;
    // The Function is responsible for handling out new numbers for BasicBlocks,
    // Operations, and virtual registers.  Numbers are assigned sequentially
    // relative to when the new number is requested.  The next 3 variables
    // store the value of the last (and thus max) number handed out so far.
  private int maxBlockNum;
  private int maxOperNum;
  private int maxRegNum;
    // A central paradigm for code generation is the concept of the current
    // block.  The Function tracks the current block, and the code generation
    // routines access this variable to determine where to put the code they
    // generate.  Each code generation method is responsible for maintaining
    // the current block
  private BasicBlock currBlock;
    // A return statement will create a return block, or if no return statement
    // is present, a default returnBlock must be generated.  This variable
    // maintains a reference to the returnBlock, allowing the code generator to
    // keep track of whether a return block has been generated yet.  A low-level
    // function only has a single return block, i.e., a single exit point.
  private BasicBlock returnBlock;
    // The "else" portion of if statements is created off the main compilation
    // path.  As multiple blocks of "else" code are generated, a place is needed
    // to store these blocks.  The next 2 variables act as head/tail for a list
    // of unconnected blocks.
  private BasicBlock firstUnconnectedBlock;
  private BasicBlock lastUnconnectedBlock;
    // controls whether any optimization should be done during code generation
  private static boolean optimize;

  private HashMap symbolTable;

    // op nums of variables for which we have a live range (def-use).  Live
    // range is a set of op nums this live range spans
  private BitArraySet []defUseVars;
  private BitArraySet []livenessVars;
  private Operation []operPtr;

/***************************************************************************/
    /**
     * @param newType is the return type of the function
     * @param newName is the name of the function
     */
  public Function(int newType, String newName) {
    this (newType, newName, null);
  }

    /**
     * @param newType is the return type of the function
     * @param newName is the name of the function
     * @param param is a reference to the first FuncParam
     */
  public Function(int newType, String newName, FuncParam param) {
    funcType = newType;
    funcName = newName;
    firstParam = param;
    firstBlock = null;
    lastBlock = null;
    localVarSize = 0;
    spillSize = 0;
    frameSize = 0;
    maxBlockNum = 0;
    maxOperNum = 0;
    currBlock = null;
    symbolTable = new HashMap();
    maxRegNum = 0;
    returnBlock = genReturnBlock();
    firstUnconnectedBlock = null;
    lastUnconnectedBlock = null;
    optimize = false;
  }
/***************************************************************************/
    // accessor methods


  public int getType () {
    return funcType;
  }
  public void setType (int newType) {
    funcType = newType;
  }
  public String getName () {
    return funcName;
  }
  public void setname (String newName) {
    funcName = newName;
  }
  public int getVarSize () {
    return localVarSize;
  }
  public void setVarSize (int size) {
    localVarSize = size;
  }
  public int getFrameSize () {
    return frameSize;
  }
  public void setFrameSize (int size) {
    frameSize = size;
  }

  /**
   *
   * @return BasicBlock which is the current block; i.e., the block
   * that code should be placed within during code generation
   */
  public BasicBlock getCurrBlock () {
    return currBlock;
  }
  /**
   *
   * @return BasicBlock which is the first block in the main chain
   */
  public BasicBlock getFirstBlock () {
    return firstBlock;
  }
  /**
   *
   * @return BasicBlock which is the last block in the main chain
   */
  public BasicBlock getLastBlock () {
    return lastBlock;
  }
  /**
   *
   * @param block BasicBlock to be used as the current block; i.e., the block
   * that code should be placed within during code generation
   */
  public void setCurrBlock (BasicBlock block) {
    currBlock = block;
  }
  public void setLastBlock (BasicBlock block) {
    lastBlock = block;
  }
  /**
   *
   * @return the head of the linked list of FuncParams
   */
  public FuncParam getfirstParam () {
    return firstParam;
  }
  /**
   *
   * @param param - the FuncParam which is the head of the list of
   * FuncParams
   */
  public void setFirstParam (FuncParam param) {
    firstParam = param;
  }
  /**
   *
   * @return a reference to the function's local symbol table
   */
  public HashMap getTable () {
    return symbolTable;
  }

  public boolean getOptimize() {
    return optimize;
  }
  public void setOptimize (boolean opt) {
    optimize = opt;
  }
  /**
   * This block is created by the function constructor, and should be the
   * single exit point for the function; i.e., all return statements should
   * generate jumps to this block.
   *
   * @return a reference to the function's return block.  This block is created
   * by the function constructor, and should be the single exit point for the
   * function; i.e., all return statements should generate jumps to this block.
   */
  public BasicBlock getReturnBlock() {
    return returnBlock;
  }

  public BasicBlock getFirstUnconnectedBlock() {
    return firstUnconnectedBlock;
  }

  public void setFirstUnconnectedBlock (BasicBlock block) {
    firstUnconnectedBlock = block;
    lastUnconnectedBlock = block;
  }

  public int getMaxRegNum () {
    return maxRegNum;
  }

  public int getMaxBlockNum () {
    return maxBlockNum;
  }

  public int getMaxOperNum () {
    return maxOperNum;
  }

/***************************************************************************/
  // support methods

    // The next three methods are for getting new BasicBlock, Operation, and
    // register numbers.  They simply pre-increment the instance variable which
    // keeps track of the largest number given out so far

   /**
     * This method is used by the Block constructor to number blocks in
     * order created.  It probably should only be used by this constructor.
     * @return a new Block number
     */
    public int getNewBlockNum () {
    return ++maxBlockNum;
  }
  /**
   * This method is used by the Operation constructor to number operations in
   * order created.  It probably should only be used by this constructor.
   * @return int
   */
  public int getNewOperNum () {
    return ++maxOperNum;
  }
  /**
   * This method generates the next sequential register number.  It will be
   * used by the code generator to get a new virtual register number.
   * @return int
   */
  public int getNewRegNum () {
    return ++maxRegNum;
  }

  /**
   *
   * This method automates the creation of the first basic block, which should
   * start with the FUNC_ENTRY operation.  Subsequent code can be added to
   * this block, or the user could decide to close this block out and add any
   * code to a subsequent block.  It should be added to the main chain of BBs.
  */
  public void createBlock0 () {
    firstBlock = new BasicBlock(this);
    lastBlock = firstBlock;
    Operation newOper =
          new Operation(Operation.OperationType.FUNC_ENTRY, firstBlock);

    firstBlock.appendOper(newOper);
  }

  /**
   * Generates return block.
   * @return new block which serves as single exit point of function.
   *
   * Each function has a single exit point, the return block.  When a return
   * statement is executed, it needs to generate this return block if it has
   * not already been created.  This method automates the creation of the
   * return block.  The user should not need to add further code to this
   * block.
   */
  public BasicBlock genReturnBlock() {
    returnBlock = new BasicBlock(this);
    Operation newOper =
            new Operation(Operation.OperationType.FUNC_EXIT, returnBlock);
    returnBlock.appendOper(newOper);
    Operation newOper2 =
            new Operation(Operation.OperationType.RETURN, returnBlock);
    Operand src = new Operand(Operand.OperandType.MACRO,"RetReg");
    newOper2.setSrcOperand(0, src);
    returnBlock.appendOper(newOper2);

    return returnBlock;
  }

    // The next 3 methods support making linked lists of blocks.  The function
    // maintains 2 lists: the main path through the code, and the list of "else"
    // blocks.

    /**
     *
     * @param newBlock - block to be appended to main chain.
     * This method supports adding a block to the "main" path through code.  It
     * will probably not be used too frequently.
    */
  public void appendBlock(BasicBlock newBlock) {
    lastBlock.setNextBlock(newBlock);
    newBlock.setPrevBlock(lastBlock);
    BasicBlock curr = newBlock;
      // in case the new block is actually the head of a list of blocks to be
      // appended, this code ensures lastBlock variable maintained.  This is
      // needed at the end of code generation for a function, when the
      // "unconnnected" list is appended to the "main" path.
    while (curr.getNextBlock() != null) {
      curr = curr.getNextBlock();
    }
    lastBlock = curr;
  }

  /**
   * Appends a block to the function's current block
   * @param newBlock - block to be appended to the current block
   * This method will be the most frequently used of the 3.  It is used when
   * you don't know if you are generating code for the "main" path or the
   * "unconnected" path - you just know that you need to append relative to
   * the current block.
  */
  public void appendToCurrentBlock(BasicBlock newBlock) {
    BasicBlock currBlock = getCurrBlock();
    currBlock.setNextBlock(newBlock);
    newBlock.setPrevBlock(currBlock);
    BasicBlock lastInChain = newBlock;
      // code added so that if you are appending a chain, the lastBlock or
      // lastUnconnectedBlock pointer is set correctly.
    while (lastInChain.getNextBlock() != null) {
      lastInChain = lastInChain.getNextBlock();
    }
    if (lastBlock == currBlock) {
      lastBlock = lastInChain;
    }
    if (lastUnconnectedBlock == currBlock) {
      lastUnconnectedBlock = lastInChain;
    }
  }
  /**
   * Unconditionally appends to "unconnnected chain"
   * @param newBlock - block to be appended to unconnected chain
   * This method is used when you know the block you are appending definitely
   * goes on the "unconnected" list.  An example might be when you put your
   * "else" block on the unconnected list.
   */

  public void appendUnconnectedBlock(BasicBlock newBlock) {
    if (lastUnconnectedBlock != null) {
      lastUnconnectedBlock.setNextBlock(newBlock);
      newBlock.setPrevBlock(lastUnconnectedBlock);
    }
    else {
      firstUnconnectedBlock = newBlock;
    }
    BasicBlock lastInChain = newBlock;

      // code added so that if you are appending a chain, the lastBlock or
      // lastUnconnectedBlock pointer is set correctly.
    while (lastInChain.getNextBlock() != null) {
      lastInChain = lastInChain.getNextBlock();
    }
    lastUnconnectedBlock = lastInChain;
  }

  public void removeBlock(BasicBlock block) {
    if (block.getPrevBlock() != null) {
      block.getPrevBlock().setNextBlock(block.getNextBlock());
    }
    else {
      firstBlock = block.getNextBlock();
    }
    if (block.getNextBlock() != null) {
      block.getNextBlock().setPrevBlock(block.getPrevBlock());
    }
    else {
      lastBlock = block.getPrevBlock();
    }
  }

  public void insertBlockAfter (BasicBlock currBlock, BasicBlock newBlock) {
    BasicBlock next = currBlock.getNextBlock();
    currBlock.setNextBlock(newBlock);
    newBlock.setPrevBlock(currBlock);
    if (next != null) {
      newBlock.setNextBlock(next);
      next.setPrevBlock(newBlock);
    }
    else {
      if (lastBlock == currBlock) {
        lastBlock = newBlock;
      }
      if (lastUnconnectedBlock == currBlock) {
        lastUnconnectedBlock = newBlock;
      }
    }
  }

/***************************************************************************/
    // dataflow support

  public BitArraySet getDefUseSetForNum (int num) {
    return livenessVars[num];
  }
  public void setDefUseSetForNum (int num, BitArraySet set) {
    livenessVars[num] = set;
  }
  public BitArraySet getLivenessSetForNum (int num) {
    return livenessVars[num];
  }
  public void setLivenessSetForNum (int num, BitArraySet set) {
    livenessVars[num] = set;
  }

  public Operation getOperForNum (int num) {
    if (operPtr != null) {
      return operPtr[num];
    }
    else
      return null;
  }

    // this simply walks through all opers, adding a ptr to the oper in the
    // array operPtr
  public void makeOperConversionArray () {
    operPtr = new Operation[maxOperNum + 1];
    for (BasicBlock currBlock = getFirstBlock(); currBlock != null;
                    currBlock = currBlock.getNextBlock()) {
      for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                    currOper = currOper.getNextOper()) {
        operPtr[currOper.getNum()] = currOper;
      }
    }
  }

    // sets up BitArraySets in function and in opers (the same BitArraySet
    // is referenced in both the function and the oper)
    // also sets up BitArraySets in each BasicBlock
  public void setUpDefUse () {
    defUseVars = new BitArraySet[maxOperNum + 1];

    for (BasicBlock currBlock = getFirstBlock(); currBlock != null;
                    currBlock = currBlock.getNextBlock()) {
        // within each block, initialize defUse sets
      currBlock.setDefUseIn(new BitArraySet(maxOperNum+1));
      currBlock.setDefUseOut(new BitArraySet(maxOperNum+1));

      for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                    currOper = currOper.getNextOper()) {
          // only make set if operation defines a reg or macro dest
        if (currOper.hasRegDest()) {
          BitArraySet newSet = new BitArraySet(maxOperNum + 1);
          currOper.setLiveRange(newSet);
          defUseVars[currOper.getNum()] = newSet;
        }
      }
    }
  }


  public void setUpLiveness () {
    livenessVars = new BitArraySet[maxOperNum + 1];

    for (BasicBlock currBlock = getFirstBlock(); currBlock != null;
                    currBlock = currBlock.getNextBlock()) {
        // within each block, initialize defUse sets
      currBlock.setLivenessIn(new BitArraySet(maxRegNum+1));
      currBlock.setLivenessOut(new BitArraySet(maxRegNum+1));

      for (Operation currOper = currBlock.getFirstOper(); currOper != null;
                    currOper = currOper.getNextOper()) {
          // only make set if operation defines a reg or macro dest
        if (currOper.hasRegDest()) {
          BitArraySet newSet = new BitArraySet(maxRegNum + 1);
          currOper.setLiveRange(newSet);
          livenessVars[currOper.getNum()] = newSet;
        }
      }
    }
  }

/***************************************************************************/
    // recursive routine which prints the function information
  public void printLLCode(PrintWriter outFile) {
    if (outFile == null) {
      System.out.print("(FUNCTION  " + getName() + "  [");
      for (FuncParam curr = firstParam; curr != null; curr = curr.getNextParam()) {
        if (curr != firstParam) {
          System.out.print(" ");
        }
        System.out.print("(" + curr.printType() + " " + curr.getName() + ")");
      }
      System.out.println("]");
      for (BasicBlock curr = firstBlock; curr != null; curr=curr.getNextBlock()) {
        curr.printLLCode(outFile);
      }
      System.out.println(")");
    }
    else {
      outFile.print("(FUNCTION  " + getName() + "  [");
      for (FuncParam curr = firstParam; curr != null; curr = curr.getNextParam()) {
        if (curr != firstParam) {
          outFile.print(" ");
        }
        outFile.print("(" + curr.printType() + " " + curr.getName() + ")");
      }
      outFile.println("]");
      for (BasicBlock curr = firstBlock; curr != null; curr=curr.getNextBlock()) {
        curr.printLLCode(outFile);
      }
      outFile.println(")");
    }

    if (this.getNextItem() != null) {
      this.getNextItem().printLLCode(outFile);
    }
  }

}
