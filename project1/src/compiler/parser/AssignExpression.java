package compiler.parser;
import lowlevel.*;

/**
 * This class is a subclass of Expression. 
 * It contains expressions in the form a = b;
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class AssignExpression extends Expression {

    private VarExpressions assignto;
    private Expression assignfrom;

    /**
     * The constructor for Assign expression
     * 
     * @param to The expression being assigned to
     * @param from The expression being assigned from
     */
    public AssignExpression(VarExpressions to, Expression from) {
        assignto = to;
        assignfrom = from;
    }

    @Override
    /**
     * This function prints out an assign expression in an abstract syntax tree.
     */
    public void print(String x) {
        System.out.println(x + "=");
        assignto.print(x + "    ");
        assignfrom.print(x + "    ");
    }
    
    public void genCode(Function f){
        assignto.genCode(f);
        assignfrom.genCode(f);
        int destReg = assignto.getRegNum();
        int srcReg = assignfrom.getRegNum();
        
        Operation op = new Operation(Operation.OperationType.ASSIGN, f.getCurrBlock());
        
        Operand srcOp;
        if(assignfrom instanceof NumExpression){
            srcOp = new Operand(Operand.OperandType.INTEGER, ((NumExpression)assignfrom).getNum());
        }else{
            srcOp = new Operand(Operand.OperandType.REGISTER, srcReg);
        }
        Operand destOp = new Operand(Operand.OperandType.REGISTER, destReg);
        op.setSrcOperand(0, srcOp);
        op.setDestOperand(0, destOp);
        
        f.getCurrBlock().appendOper(op);
    }
}
