package lowlevel;

import java.io.*;

/**
 * This class is abstracts an operand, one of the arguments to an Operation
 *
 * @author Dr. Gallagher
 * @version 1.0
 * Created: 22 Apr 03
 * Summary of Modifications:
 *
 * Description:  An Operand is one of the building blocks of an Operation.  An
 * Operation is made up of src and dest Operands.  For example, r1=r2+3 has r2
 * and 3 as src operands, and r1 as the dest.  Because there are several types
 * of Operands possible, each Operand must specify its type.  Because the
 * value of the Operand varies from int to Strings, the value is stored as an
 * Object
 */



public class Operand {
    /**
     * OperandType specifies the different types of operands
     * UNKNOWN shouldn't be used
     * INTEGER is for integer literals
     *    - uses a Integer value
     * REGISTER is for virtual register operands
     *    - uses a Integer value, representing register number
     * MACRO is for special purpose registers, such as architectural regs
     *    - uses a String value, which is name of register
     * BLOCK is for destinations of a JMP or BR
     *    - uses a Integer value, representing block number
     * STRING is for functions names (in JSRs) or global variable names
     *    - uses a String value, which is name of function or variable
     */
  public enum OperandType {UNKNOWN, INTEGER, REGISTER, MACRO, BLOCK, STRING};

    // these are the old pre 1.5 constants
    // constants to define the different types of operands
//  public static final int OPERAND_UNKNOWN = 0;
    // an integer literal
//  public static final int OPERAND_INT = 1;
    // a register (assumed for now to be integer reg)
//  public static final int OPERAND_REG = 2;
    // a special purpose register, with a specific name
//  public static final int OPERAND_MACRO = 3;
    // an operand for a BasicBlock, where Operand is tgt of a JMP/BR
//  public static final int OPERAND_BLOCK = 4;
    // String operands are useful for function names or string constants
// public static final int OPERAND_STRING = 5;
/***************************************************************************/
  // instance variables
    // the operand type, as specified in consts above
  OperandType type;
    // contains an int for INT, REG, & BLOCK.  Contains a String for MACRO and
    // STRING
  Object value;
/***************************************************************************/
  // constructors
    /**
     * @param newType specifies the type of the Operand
     */
  public Operand (OperandType newType) {
    this (newType, null);
  }
    /**
     * Creates a new Operand; this will likely be the most common form of the
     * constructor used by the code generator.
     * @param newType specifies the type of the Operand
     * @param newValue specifies the value of the Operand
     */
  public Operand (OperandType newType, Object newValue) {
    type = newType;
    value = newValue;
  }
    /**
     * @param copyOperand will have its type/value copied
     */
  public Operand (Operand copyOperand) {
    type = copyOperand.getType();
      // we can share the same Object
    value = copyOperand.getValue();
  }

/***************************************************************************/
  // accessor methods
  public OperandType getType () {
    return type;
  }
  public void setType (OperandType newType) {
    type = newType;
  }
  public Object getValue() {
    return value;
  }
  public void setValue( Object newValue) {
    value = newValue;
  }
/***************************************************************************/
  // support methods
    // converts type to a string for printing
  private String printType () {
    if (type == OperandType.INTEGER) {
      return ("i");
    }
    else if (type == OperandType.REGISTER) {
      return ("r");
    }
    else if (type == OperandType.MACRO) {
      return ("m");
    }
    else if (type == OperandType.BLOCK) {
      return ("bb");
    }
    else if (type == OperandType.STRING) {
      return ("s");
    }
    else {
      throw new parser.CodeGenerationException("Operand: invalid type");
    }
  }
    // prints an operand surrounded by parentheses
  public void printLLCode(PrintWriter outFile) {
    if (outFile == null) {
      System.out.print("("+ printType() +" " + value + ")");
    }
    else {
      outFile.print("("+ printType() +" " + value + ")");
    }
//    System.out.print("("+ printType() +" " + value + ")");
  }
}
