package compiler.parser;
import lowlevel.*;

/**
 * This class is a subclass of Statement.
 * It contains return statements
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class ReturnStatement extends Statement {

    private Expression expression;

    /**
     * Return statement constructor
     * 
     * @param e the expression being returned
     */
    public ReturnStatement(Expression e) {
        expression = e;
    }

    @Override
    /**
     * Print function for return statement
     */
    public void print(String x) {
        System.out.println(x + "return: ");
        if (expression != null) {
            expression.print(x + "    ");
        } else {
            System.out.println(x + "    void");;
        }
    }
    
    public void genCode(Function f) {
        if (expression != null) {
            expression.genCode(f);
            Operand retreg = new Operand(Operand.OperandType.MACRO, "RetReg");
            Operand retExp;
            if (expression instanceof NumExpression) {
                retExp = new Operand(Operand.OperandType.INTEGER, ((NumExpression) expression).getNum());
            } else {
                retExp = new Operand(Operand.OperandType.REGISTER, expression.getRegNum());
            }

            //Assign return expression to RetReg
            Operation op = new Operation(Operation.OperationType.ASSIGN, f.getCurrBlock());
            op.setDestOperand(0, retreg);
            op.setSrcOperand(0, retExp);

            f.getCurrBlock().appendOper(op);
        }
        //jump to return block
        Operation retJump = new Operation(Operation.OperationType.JMP, f.getCurrBlock());
        Operand jmpOper = new Operand(Operand.OperandType.BLOCK, f.getReturnBlock().getBlockNum());
        retJump.setSrcOperand(0, jmpOper);
        f.getCurrBlock().appendOper(retJump);
    }
}
