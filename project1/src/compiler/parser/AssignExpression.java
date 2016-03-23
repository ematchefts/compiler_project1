package compiler.parser;

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
}
