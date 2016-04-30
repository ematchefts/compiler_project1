package lowlevel;

import java.io.*;
import dataflow.BitArraySet;

/**
 * This class is the primary low-level abstraction for an assembly instruction
 *
 * @author Dr. Gallagher
 * @version 1.0
 * Created: 22 Apr 03
 * Summary of Modifications:
 *    29 Apr 03 - DMG - New constructor added which just takes type as an arg
 *       We don't need to pass the block anymore, because the appendOper()
 *       method of the BasicBlock will set the Operations block
 *    1 May 03 - DMG  - Fixed bug in new constructor added 29 Apr
 *
 * Description:  An Operation is the basic internal representation of an
 * assembly language instruction.  Early in the back-end, Operations are
 * generic and don't necessarily map 1:1 with assembly.  As the low-level code
 * gets more architecture-specific, Operations should become more and more a 1:1
 * mapping with assembly.  An Operation maintains next/prev references, so a
 * block can contain a list of Operations.  The Operation also maintains sets
 * of Operands.  To keep it general, we employ arrays of src and dest Operands.
 */

public class Operation {

  public enum OperationType {UNKNOWN, FUNC_ENTRY, FUNC_EXIT, ASSIGN, ADD_I,
        SUB_I, MUL_I, DIV_I, LT, LTE, GT, GTE, EQUAL, NOT_EQUAL, RETURN, JMP,
        BEQ, BNE, PASS, CALL, LOAD_I, STORE_I, X64_ADD_Q, X64_SUB_Q, X64_LOAD_Q, X64_STORE_Q,
        X86_MUL_I, X86_DIV_I, X86_BEQ, X86_BNE, X86_BLT, X86_BLE, X86_BGT,
        X86_BGE, X86_PUSH, X86_POP, X86_MOV, X86_CMP
  }

    // Constants defined to allow the # of Operands to be tailored to a specific
    // architecture
  public static final int MAX_DEST_OPERANDS = 2;
  public static final int MAX_SRC_OPERANDS = 4;


/***************************************************************************/
    // instance variables
    // the block containing the Operation
  private BasicBlock block;
    // references to maintain the linked list of Operations within the block
  private Operation prevOper;
  private Operation nextOper;
    // A unique number identifying the Operation; set in constructor
  private int opNum;
    // The type of Operation, as defined in consts above
  private OperationType opType;
    // Arrays of src and destination Operands; Their size is determined by
    // the consts above
  private Operand []dest;
  private Operand []src;
    // the next two variables currently unused
  private int maxSrc;
  private int maxDest;

    // FOR LIVE RANGE
  private BitArraySet liveRange;

    // future use
  private Attribute attr;
/***************************************************************************/
  // constructors
    /**
     * Primary constructor for Operations
     * @param type is the Operation type (e.g.,OPER_JMP)
     * @param currBlock is the block containing the Operation
     */
  public Operation (OperationType type, BasicBlock currBlock) {
    opNum = currBlock.getFunc().getNewOperNum();
    opType = type;
    block = currBlock;
    prevOper = null;
    nextOper = null;
    dest = new Operand[MAX_DEST_OPERANDS];
    src = new Operand[MAX_SRC_OPERANDS];
    maxSrc = -1;
    maxDest = -1;

  }

/***************************************************************************/
  // accessor methods
  public int getNum () {
    return opNum;
  }
  public void setNum(int newNum) {
    opNum = newNum;
  }
  public OperationType getType () {
    return opType;
  }
  public void setType (OperationType newType) {
    opType = newType;
  }

  public BasicBlock getBlock () {
    return block;
  }
  public void setBlock (BasicBlock newBlock) {
    block = newBlock;
  }

  public Operation getPrevOper () {
    return prevOper;
  }
  public void setPrevOper (Operation prev) {
    prevOper = prev;
  }
  public Operation getNextOper () {
    return nextOper;
  }
  public void setNextOper (Operation next) {
    nextOper = next;
  }

  public Operand getSrcOperand (int index) {
    return src[index];
  }
  public void setSrcOperand (int index, Operand newOperand) {
    src[index] = newOperand;
    if (index > maxSrc) {
      maxSrc = index;
    }
  }
  public Operand getDestOperand (int index) {
    return dest[index];
  }
  public void setDestOperand (int index, Operand newOperand) {
    dest[index] = newOperand;
    if (index > maxDest) {
      maxDest = index;
    }
  }

  public BitArraySet getLiveRange () {
    return liveRange;
  }
  public void setLiveRange (BitArraySet newSet) {
    liveRange = newSet;
  }

  public Attribute getAttribute() {
    return attr;
  }

  public void addAttribute(Attribute newAttr) {
      // just put at head of list
    newAttr.setNext(attr);
    attr = newAttr;
  }

  public boolean hasAttribute (String name) {
    boolean retVal = false;

    for (Attribute currAttr = attr; currAttr != null;
          currAttr = currAttr.getNext() ) {
      if (name.compareTo(currAttr.getName()) == 0) {
        retVal = true;
        break;
      }
    }
    return retVal;
  }

  public String findAttribute (String name) {
      // searches for match of name and returns value or null
    String retVal = null;

    for (Attribute currAttr = attr; currAttr != null;
          currAttr = currAttr.getNext() ) {
      if (name.compareTo(currAttr.getName()) == 0) {
        retVal = currAttr.getValue();
        break;
      }
    }
    return retVal;
  }

/***************************************************************************/
  // support methods

  public void delete() {
    if (getPrevOper() == null) {
      getBlock().setFirstOper(getNextOper());
    }
    else {
      getPrevOper().setNextOper(getNextOper());
    }
    if (getNextOper() == null) {
      getBlock().setLastOper(getPrevOper());
    }
    else {
      getNextOper().setPrevOper(getPrevOper());
    }
  }

  public boolean hasRegDest() {
    if (dest[0] != null) {
      if (dest[0].getType() == Operand.OperandType.REGISTER) {
        return true;
      }
    }
    return false;
  }

    // converts operation type into a string for printing
  public String printOperType() {
    switch (opType) {
      case FUNC_ENTRY:
        return "Func_Entry";
      case FUNC_EXIT:
        return "Func_Exit";
      case ASSIGN:
        return "Mov";
      case ADD_I:
        return "Add_I";
      case SUB_I:
        return "Sub_I";
      case MUL_I:
        return "Mul_I";
      case DIV_I:
        return "Div_I";
      case LT:
        return "LT";
      case LTE:
        return "LTE";
      case GT:
        return "GT";
      case GTE:
        return "GTE";
      case EQUAL:
        return "EQ";
      case NOT_EQUAL:
        return "NEQ";
      case RETURN:
        return "Return";
      case JMP:
        return "Jmp";
      case PASS:
        return "Pass";
      case CALL:
        return "JSR";
      case BEQ:
        return "BEQ";
      case BNE:
        return "BNE";
      case LOAD_I:
        return "Load";
      case STORE_I:
        return "Store";
      case X86_PUSH:
        return "Push";
      case X86_POP:
        return "Pop";
      case X86_MOV:
        return "Mov";
      case X86_CMP:
        return "Cmp";
      case X86_BEQ:
        return "BEQ";
      case X86_BNE:
        return "BNE";
      case X86_BLT:
        return "BLT";
      case X86_BLE:
        return "BLE";
      case X86_BGT:
        return "BGT";
      case X86_BGE:
        return "BGE";
      case X86_MUL_I:
        return "Mul";
      case X86_DIV_I:
        return "Div";
      case X64_ADD_Q:
          return "Add_Q";
      case X64_SUB_Q:
          return "Sub_Q";
      case X64_LOAD_Q:
          return "Load_Q";
      case X64_STORE_Q:
          return "Store_Q";

      default:
        throw new LowLevelException ("Operation: unexpected op type");
    }
  }

  public boolean isBranchOper() {
    return ( (opType == OperationType.BEQ) ||
             (opType == OperationType.BNE) ||
             (opType == OperationType.X86_BEQ) ||
             (opType == OperationType.X86_BNE) ||
             (opType == OperationType.X86_BLT) ||
             (opType == OperationType.X86_BLE) ||
             (opType == OperationType.X86_BGT) ||
             (opType == OperationType.X86_BGE) );
  }

  public boolean isX86BranchOper() {
    return ( (opType == OperationType.X86_BEQ) ||
             (opType == OperationType.X86_BNE) ||
             (opType == OperationType.X86_BLT) ||
             (opType == OperationType.X86_BLE) ||
             (opType == OperationType.X86_BGT) ||
             (opType == OperationType.X86_BGE) );
  }

    // prints the Operation, recursively calling print on each Operand
  public void printLLCode(PrintWriter outFile) {
    if (outFile == null) {
      System.out.print("    (OPER " + this.getNum() + " " + printOperType() + " [");
      for (int currDest = 0; currDest <= maxDest; currDest++) {
        if (dest[currDest] != null) {
          dest[currDest].printLLCode(outFile);
        }
        else {
          System.out.println("()");
        }
      }
      System.out.print("]  [");
      for (int currSrc = 0; currSrc <= maxSrc; currSrc++) {
        if (src[currSrc] != null) {
          src[currSrc].printLLCode(outFile);
        }
        else {
          System.out.println("()");
        }
      }
      System.out.print("]");
      System.out.println(")");
    }
    else {
      outFile.print("    (OPER " + this.getNum() + " " + printOperType() + " [");
      for (int currDest = 0; currDest <= maxDest; currDest++) {
        if (dest[currDest] != null) {
          dest[currDest].printLLCode(outFile);
        }
        else {
          outFile.println("()");
        }
      }
      outFile.print("]  [");
      for (int currSrc = 0; currSrc <= maxSrc; currSrc++) {
        if (src[currSrc] != null) {
          src[currSrc].printLLCode(outFile);
        }
        else {
          outFile.println("()");
        }
      }
      outFile.print("]");
      outFile.println(")");
    }
  }


}
