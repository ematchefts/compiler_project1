package compiler.parser;
import lowlevel.*;

/**
 * This class is a subclass of Statement.
 * It contains while statements
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class WhileStatement extends Statement {

    private Expression expression;
    private Statement statement;

    /**
     * While statement constructor
     * 
     * @param e The expression of the while statement condition
     * @param st The statement inside of the while statement
     */
    public WhileStatement(Expression e, Statement st) {
        expression = e; 
        statement = st;
    }

    @Override
    public void print(String x) {
        System.out.println(x + "while(");
        expression.print(x + "    ");
        System.out.println(x + ")");
        System.out.println(x + "do:");
        statement.print(x + "    ");
    }
    
    public void genCode(Function f) {

        //declare blocks
        BasicBlock whileBlock = new BasicBlock(f);
        BasicBlock thenBlock = new BasicBlock(f);
        BasicBlock postBlock = new BasicBlock(f);

        f.appendToCurrentBlock(whileBlock);
        f.setCurrBlock(whileBlock);
        
        //Create branch to else (or post) block operation
        expression.genCode(f);
        Operand whileExpr = new Operand(Operand.OperandType.REGISTER, expression.getRegNum());
        Operation postJmp = new Operation(Operation.OperationType.BEQ, f.getCurrBlock());
        postJmp.setSrcOperand(0, whileExpr);
        
        postJmp.setSrcOperand(1, new Operand(Operand.OperandType.INTEGER, 0));
        postJmp.setSrcOperand(2, new Operand(Operand.OperandType.BLOCK, postBlock.getBlockNum()));
        f.getCurrBlock().appendOper(postJmp);

        //append THEN and generate statement
        f.appendToCurrentBlock(thenBlock);
        f.setCurrBlock(thenBlock);
        statement.genCode(f);

        //create branch back to beginning
        whileExpr = new Operand(Operand.OperandType.REGISTER, expression.getRegNum());
        Operation iterJmp = new Operation(Operation.OperationType.BNE, f.getCurrBlock());
        iterJmp.setSrcOperand(0, whileExpr);
        iterJmp.setSrcOperand(1, new Operand(Operand.OperandType.INTEGER, 0));
        iterJmp.setSrcOperand(2, new Operand(Operand.OperandType.BLOCK, whileBlock.getBlockNum()));
        f.getCurrBlock().appendOper(iterJmp);

        //append POST
        f.appendToCurrentBlock(postBlock);
        f.setCurrBlock(postBlock);
    }
}
