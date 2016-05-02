package compiler.parser;

import lowlevel.*;

/**
 * This class is a subclass of Statement.
 * It contains if statements
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class IfStatement extends Statement {

    private Expression expression;
    private Statement statement;
    private Statement elseStatement;

    /**
     * If statement constructor
     * 
     * @param e the expression of the if statement condition
     * @param s The statement inside the if statement
     * @param es The else statement 
     */
    public IfStatement(Expression e, Statement s, Statement es) {
        expression = e;
        statement = s;
        elseStatement = es;
    }

    /**
     * Print function for if statement
     */
    @Override
    public void print(String w) {
        System.out.println(w + "if (");
        expression.print(w + "    ");
        System.out.print(w + ")\n" + w + "then:\n");
        statement.print(w + "    ");
        if (elseStatement != null) {
            System.out.println(w + "else:");
            elseStatement.print(w + "    ");
        }
    }
    
    public void genCode(Function f) {

        //declare blocks
        BasicBlock mythenBlock = new BasicBlock(f);
        BasicBlock mypostBlock = new BasicBlock(f);
        BasicBlock myelseBlock = new BasicBlock(f);

        //Create branch to else (or post) block operation
        expression.genCode(f);
        Operand ifExpr = new Operand(Operand.OperandType.REGISTER, expression.getRegNum());
        Operation elseJmp = new Operation(Operation.OperationType.BEQ, f.getCurrBlock());
        elseJmp.setSrcOperand(0, ifExpr);
        
        elseJmp.setSrcOperand(1, new Operand(Operand.OperandType.INTEGER, 0));
        if (elseStatement != null) {
            elseJmp.setSrcOperand(2, new Operand(Operand.OperandType.BLOCK, myelseBlock.getBlockNum()));
        } else {
            elseJmp.setSrcOperand(2, new Operand(Operand.OperandType.BLOCK, mypostBlock.getBlockNum()));
        }
        f.getCurrBlock().appendOper(elseJmp);

        //append THEN and generate statement
        f.appendToCurrentBlock(mythenBlock);
        f.setCurrBlock(mythenBlock);
        statement.genCode(f);

        //append POST
        f.appendToCurrentBlock(mypostBlock);

        //generate and append ELSE statement
        if (elseStatement != null) {
            f.setCurrBlock(myelseBlock);
            elseStatement.genCode(f);
            
            //append jump back to POST
            Operation jump = new Operation(Operation.OperationType.JMP, f.getCurrBlock());
            jump.setSrcOperand(0, new Operand(Operand.OperandType.BLOCK, mypostBlock.getBlockNum()));
            myelseBlock.appendOper(jump);
            
            f.appendUnconnectedBlock(myelseBlock);
        }
        
        //generate POST statement
        f.setCurrBlock(mypostBlock);
    }
}
