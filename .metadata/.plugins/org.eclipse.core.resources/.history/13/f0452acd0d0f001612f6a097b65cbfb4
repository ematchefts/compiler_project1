package compiler.parser;
import lowlevel.*;
import lowlevel.Operation.OperationType;

/**
 * This class is a subclass of Expression.
 * It contains expressions of the form a operator b
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class BinaryExpression extends Expression {

    private Expression operand1;
    private Expression operand2;
    private Operation op;

    /**
     * Binary Expression constructor 
     * 
     * @param op1 expression of the first operand
     * @param op2 expression of the second operand
     * @param o The operation between the first and second operand
     */
    public BinaryExpression(Expression op1, Expression op2, Operation o) {
        operand1 = op1;
        operand2 = op2;
        op = o;
    }
    
    public Operation.OperationType getOpType() {
        return op.getType();
    }

    /**
     * This function prints a Binary Expression in an abstract syntax tree
     */
    @Override
    public void print(String x) {
        System.out.println(x + op.toString());
        operand1.print(x + "    ");
        operand2.print(x + "    ");
    }
    
    public void genCode(Function f) {

        operand1.genCode(f);
        operand2.genCode(f);

        Operand leftReg;
        Operand rightReg;
        if (operand1 instanceof NumExpression) {
            leftReg = new Operand(Operand.OperandType.INTEGER, ((NumExpression) operand1).getNum());
        } else {
            leftReg = new Operand(Operand.OperandType.REGISTER, operand1.getRegNum());
        }
        if (operand2 instanceof NumExpression) {
            rightReg = new Operand(Operand.OperandType.INTEGER, ((NumExpression) operand2).getNum());
        } else {
            rightReg = new Operand(Operand.OperandType.REGISTER, operand2.getRegNum());
        }

        Operand destReg = new Operand(Operand.OperandType.REGISTER, f.getNewRegNum());
        setRegNum((int) destReg.getValue());

        op.setSrcOperand(0, leftReg);
        op.setSrcOperand(1, rightReg);
        op.setDestOperand(0, destReg);

        f.getCurrBlock().appendOper(op);
    }
}
