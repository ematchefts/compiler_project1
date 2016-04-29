package compiler.parser;

/**
 * This class is a subclass of statement.
 * It contains expression statements
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class ExpressionStatement extends Statement {

    private Expression expression;

    /**
     * Expression statement constructor
     * @param e the expression
     */
    public ExpressionStatement(Expression e) {
        expression = e;
    }

    /**
     * Print function for expression statement
     */
    @Override
    public void print(String w) {
        System.out.println(w + "expr stmt: ");
        if (expression != null) {
            expression.print(w + "    ");
        }
    }
}
