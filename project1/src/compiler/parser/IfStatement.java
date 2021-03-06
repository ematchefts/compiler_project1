package compiler.parser;

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
}
