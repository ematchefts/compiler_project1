package x86codegen;

import lowlevel.*;
import java.io.PrintWriter;

public class X86AssemblyGenerator {

  private static final int UNKNOWN = 0;
  private static final int DATA = 1;
  private static final int FUNCTION = 2;

  private CodeItem firstItem;
  private PrintWriter outFile;

  private int state;

  public X86AssemblyGenerator(CodeItem first, PrintWriter out) {
    firstItem = first;
    outFile = out;
    state = UNKNOWN;
  }

  public void generateAssembly() {

    for (CodeItem currItem = firstItem; currItem != null;
         currItem = currItem.getNextItem()) {
      if (currItem instanceof Data) {
        if (state != DATA) {
          outFile.println(".data");
          state = DATA;
        }
        int size = 4;
        if ( ( (Data) currItem).getIsArray()) {
          size = 4 * ( (Data) currItem).getArraySize();
        }
        outFile.println(".comm\t" + ( (Data) currItem).getName() + "," + size +
                        ",4");
        outFile.println();
      }
      else {
        Function func = (Function) currItem;
        peepholeOpti(func);
        generateFunction(func);
      }
    }
  }

  private void generateFunction(Function func) {

    if (state != FUNCTION) {
      outFile.println(".text");
      outFile.println("\t.align 4");
      state = FUNCTION;
    }
    outFile.println(".globl  " + func.getName());
    outFile.println(func.getName() + ":");

    for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
         currBlock = currBlock.getNextBlock()) {
      if (currBlock.getBlockNum() != 0) {
        outFile.println(func.getName() + "_bb" + currBlock.getBlockNum() + ":");
      }
      for (Operation currOper = currBlock.getFirstOper(); currOper != null;
           currOper = currOper.getNextOper()) {
        switch (currOper.getType()) {

          case ADD_I:
          case SUB_I:

          case X86_MUL_I:
          case X86_DIV_I:
            assembleArithmetic(currOper);
            break;

          case RETURN:
            outFile.println("\tret");
            break;

          case JMP:
            int tgt = ( (Integer) currOper.getSrcOperand(0).getValue()).
                intValue();
            outFile.println("\tjmp\t" + func.getName() + "_bb" + tgt);
            break;

          case PASS:
            outFile.print("\tpushl\t");
            Operand src = currOper.getSrcOperand(0);
            if (src.getType() == Operand.OperandType.REGISTER) {
              outFile.println("%" + ( (String) src.getValue()));
            }
            else {
              outFile.println("$" + ( (Integer) src.getValue()).intValue());
            }
            break;

          case CALL:
            outFile.println("\tcall\t" +
                            ( (String) currOper.getSrcOperand(0).getValue()));
            break;

          case LOAD_I:

            // movl  (%ebx), %eax   or   movl   a, %eax  or movl 4(%eax), %ebx
            outFile.print("\tmovl\t");
            Operand src0 = currOper.getSrcOperand(0);
            Operand src1 = currOper.getSrcOperand(1);

            // only used for stack loads (local arrays)
            Operand src2 = currOper.getSrcOperand(2);

            if (src0.getType() == Operand.OperandType.STRING) {
              outFile.print( ( (String) currOper.getSrcOperand(0).getValue()));
              if (src1 != null) {
                if (src1.getType() == Operand.OperandType.INTEGER) {
                  if ( ( (Integer) src1.getValue()).intValue() != 0) {
                    outFile.print("+" + ( (Integer) src1.getValue()).intValue());
                  }
                }
                else if (src1.getType() == Operand.OperandType.MACRO) {
                  outFile.print("(%" + src1.getValue() + ")");
                }
                else {
                  throw new X86CodegenException("assembleLoad: unexpected src1");
                }
              }
            }
            else if (src0.getType() == Operand.OperandType.MACRO) {
              if (src1 != null) {
                if (src1.getType() == Operand.OperandType.INTEGER) {
                  if ( ( (Integer) src1.getValue()).intValue() != 0) {
                    outFile.print( ( (Integer) src1.getValue()).intValue());
                  }
                  outFile.print("(%" +
                                ( (String) currOper.getSrcOperand(0).getValue()));
                  if (src2 != null) {
                    if (src2.getType() == Operand.OperandType.MACRO) {
                      outFile.print(",%" + currOper.getSrcOperand(2).getValue());
                    }
                    else {
                      throw new X86CodegenException(
                          "assembleLoad: unexpected src2");
                    }
                  }
                  outFile.print(")");

                }
                else if (src1.getType() == Operand.OperandType.MACRO) {
                  outFile.print("(%" +
                                ( (String) currOper.getSrcOperand(0).getValue()));
                  outFile.print(",%" + currOper.getSrcOperand(2).getValue());
                  outFile.print(")");
                }
                else {
                  throw new X86CodegenException("assembleLoad: unexpected src1");
                }
              }
              else {
                outFile.print("(%" +
                              ( (String) currOper.getSrcOperand(0).getValue()) +
                              ")");
              }
            }
            else {
              throw new X86CodegenException("assembleLoad: unexpected src0");
            }
            outFile.println(", %" +
                            ( (String) currOper.getDestOperand(0).getValue()));
            break;

          case STORE_I:

            // movl   %ebx, (%eax)  or  movl $2, (%eax)  or movl %eax, a
            // or movl %ebx, 8(%eax)   or movl %ebx, 8(a)
            outFile.print("\tmovl\t");
            src0 = currOper.getSrcOperand(0);
            if (src0.getType() == Operand.OperandType.INTEGER) {
              outFile.print("$" + ( (Integer) src0.getValue()).intValue() +
                            ", ");
            }
            else {
              outFile.print("%" + ( (String) src0.getValue()) + ", ");
            }
            src1 = currOper.getSrcOperand(1);
            src2 = currOper.getSrcOperand(2);
            Operand src3 = currOper.getSrcOperand(3);

            if (src1.getType() == Operand.OperandType.MACRO) {

              if (src2 != null) {
                if (src2.getType() == Operand.OperandType.INTEGER) {
                  if ( ( (Integer) src2.getValue()).intValue() != 0) {
                    outFile.print( ( (Integer) src2.getValue()).intValue());
                  }
                  outFile.print("(%" +
                                  ( (String) src1.getValue()));
                  if (src3 != null) {
                    if (src3.getType() == Operand.OperandType.MACRO) {
                      outFile.print(",%" + currOper.getSrcOperand(3).getValue());
                    }
                    else {
                      throw new X86CodegenException(
                          "assembleStore: unexpected src3");
                    }
                  }
                  outFile.println(")");
                }
                else {
                  throw new X86CodegenException(
                      "assembleStore: unexpected src2");
                }
              }
            }
            // else is global
            else {
              outFile.print( ( (String) src1.getValue()));
              if (src2 != null) {
                if (src2.getType() == Operand.OperandType.INTEGER) {
                  if ( ( (Integer) src2.getValue()).intValue() != 0) {
                    outFile.println("+" + ( (Integer) src2.getValue()).intValue());
                  }
                  else {
                    outFile.println();
                  }
                }
                else if (src2.getType() == Operand.OperandType.MACRO) {
                  outFile.println("(%" + src2.getValue() + ")");
                }

                else {
                  throw new X86CodegenException(
                      "assembleStore: unexpected src2");
                }
              }
            }
            break;

          case X86_PUSH:
            outFile.print("\tpushl\t");
            src0 = currOper.getSrcOperand(0);
            if (src0.getType() == Operand.OperandType.INTEGER) {
              outFile.println("$" + ( (Integer) src0.getValue()).intValue());
            }
            else {
              outFile.println("%" + ( (String) src0.getValue()));
            }
            break;

          case X86_POP:
            outFile.print("\tpopl\t");
            Operand dest0 = currOper.getDestOperand(0);
            if (dest0.getType() == Operand.OperandType.INTEGER) {
              outFile.println("$" + ( (Integer) dest0.getValue()).intValue());
            }
            else {
              outFile.println("%" + ( (String) dest0.getValue()));
            }
            break;

          case ASSIGN:
          case X86_MOV:

            // movl $2, %eax    or   movl  $eax, $ebx
            outFile.print("\tmovl\t");
            src0 = currOper.getSrcOperand(0);
            if (src0.getType() == Operand.OperandType.INTEGER) {
              outFile.print("$" + ( (Integer) src0.getValue()).intValue());
            }
            else {
              outFile.print("%" + ( (String) src0.getValue()));
            }
            outFile.println(", %" +
                            ( (String) currOper.getDestOperand(0).getValue()));
            break;

          case X86_CMP:
            outFile.print("\tcmpl\t");
            src1 = currOper.getSrcOperand(1);
            if (src1.getType() == Operand.OperandType.INTEGER) {
              outFile.print("$" + ( (Integer) src1.getValue()).intValue() +
                            ", ");
            }
            else {
              outFile.print("%" + ( (String) src1.getValue()) + ", ");
            }
            src0 = currOper.getSrcOperand(0);
            if (src0.getType() == Operand.OperandType.INTEGER) {
              outFile.println("$" + ( (Integer) src0.getValue()).intValue());
            }
            else {
              outFile.println("%" + ( (String) src0.getValue()));
            }
            break;

          case X86_BEQ:
          case X86_BNE:
          case X86_BLT:
          case X86_BLE:
          case X86_BGT:
          case X86_BGE:
            assembleBranch(currOper);
            break;

          case LT:
          case LTE:
          case GT:
          case GTE:
          case EQUAL:
          case NOT_EQUAL:
          case FUNC_ENTRY:
          case FUNC_EXIT:
          case BEQ:
          case BNE:
          case MUL_I:
          case DIV_I:
          default:
            throw new X86CodegenException("assembler: unknown oper type " +
                                          currOper.getType());
        }
      }
    }
  }

  private void assembleArithmetic(Operation oper) {

    if (oper.getType() == Operation.OperationType.ADD_I) {
      outFile.print("\taddl\t");
    }
    else if (oper.getType() == Operation.OperationType.SUB_I) {
      outFile.print("\tsubl\t");
    }
    else if (oper.getType() == Operation.OperationType.X86_MUL_I) {
      outFile.print("\timull\t");
    }
    else if (oper.getType() == Operation.OperationType.X86_DIV_I) {
      outFile.print("\tidivl\t");
    }
    else {
      throw new X86CodegenException("Assembler: unexpected arithmetic");
    }

    Operand src1 = oper.getSrcOperand(1);
    if (src1.getType() == Operand.OperandType.INTEGER) {
      outFile.print("$" + ( (Integer) src1.getValue()).intValue());
    }
    else {
      outFile.print("%" + ( (String) src1.getValue()));
    }
    outFile.print(", ");
    Operand src0 = oper.getSrcOperand(0);
    if (src0.getType() == Operand.OperandType.INTEGER) {
      outFile.println("$" + ( (Integer) src0.getValue()).intValue());
    }
    else {
      outFile.println("%" + ( (String) src0.getValue()));
    }

  }

//  private void assembleCompare(Operation oper) {
//
//  }

  private void assembleBranch(Operation oper) {
    switch (oper.getType()) {
      case X86_BEQ:
        outFile.print("\tje\t");
        break;
      case X86_BNE:
        outFile.print("\tjne\t");
        break;
      case X86_BLT:
        outFile.print("\tjl\t");
        break;
      case X86_BLE:
        outFile.print("\tjle\t");
        break;
      case X86_BGT:
        outFile.print("\tjg\t");
        break;
      case X86_BGE:
        outFile.print("\tjge\t");
        break;
      default:
        throw new X86CodegenException("assembleBranch: bad oper type");
    }
    outFile.println(oper.getBlock().getFunc().getName() + "_bb" +
                    ( (Integer) oper.getSrcOperand(0).getValue()).intValue());
  }

  private void peepholeOpti(Function func) {
    removeWorthlessMoves(func);
  }

  private void removeWorthlessMoves(Function func) {
    // here we look for movl %EAX, %EAX
    for (BasicBlock currBlock = func.getFirstBlock(); currBlock != null;
         currBlock = currBlock.getNextBlock()) {
      Operation nextOper;
      for (Operation currOper = currBlock.getFirstOper(); currOper != null;
           currOper = nextOper) {
        // we use nextOper here because we may be deleting currOper and would
        // be unable to follow its next ptr at end of loop
        nextOper = currOper.getNextOper();
        if ( (currOper.getType() != Operation.OperationType.ASSIGN) &&
            (currOper.getType() != Operation.OperationType.X86_MOV)) {
          continue;
        }
        if (currOper.getDestOperand(0).getType() != Operand.OperandType.MACRO) {
          continue;
        }
        if (currOper.getSrcOperand(0).getType() != Operand.OperandType.MACRO) {
          continue;
        }
        String dest = (String) currOper.getDestOperand(0).getValue();
        String src = (String) currOper.getSrcOperand(0).getValue();
        if (dest.compareTo(src) == 0) {
          currBlock.removeOper(currOper);
        }
      }
    }
  }
}
