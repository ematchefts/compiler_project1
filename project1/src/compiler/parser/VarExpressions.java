package compiler.parser;

/**
 * This class is a subclass of Expression.
 * It contains variable expressions
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class VarExpressions extends Expression {

    private String var;
    private Expression expression;

    /**
     * Variable expression constructor
     * @param s the name of the variable expression
     */
    public VarExpressions(String s) {
        this(s, null);
    }

    /**
     * Variable expression constructor
     * 
     * @param s The name of the variable exprsesion
     * @param e The expression inside the brackets of the variable expression
     */
    public VarExpressions(String s, Expression e) {
        var = s;
        expression = e;
    }

    /**
     * Getter for the name of the variable expression
     * @return var the name of the variable expression
     */
    public String getVar() {
        return var;
    }

    @Override
    /**
     * Print function for variable expressions
     */
    public void print(String x) {
        System.out.println(x + var);
        if (expression != null) {
            System.out.println(x + "[");
            expression.print(x + "    ");
            System.out.println(x + "]");
        }
    }
}
