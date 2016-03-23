package compiler.parser;


public class AssignExpression extends Expression {

    private VarExpressions assignto;
    private Expression assignfrom;

    public AssignExpression(VarExpressions to, Expression from) {
        assignto = to;
        assignfrom = from;
    }

    @Override
    public void print(String x) {
        System.out.println(x + "=");
        assignto.print(x + "    ");
        assignfrom.print(x + "    ");
    }
}
