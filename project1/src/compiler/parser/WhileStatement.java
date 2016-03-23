package compiler.parser;

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
}
