package compiler.parser;

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
}
