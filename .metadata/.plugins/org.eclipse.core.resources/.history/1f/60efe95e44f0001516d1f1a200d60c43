package compiler.parser;

public class VarExpressions extends Expression {

    private String var;
    private Expression expression;

    public VarExpressions(String s) {
        this(s, null);
    }

    public VarExpressions(String s, Expression e) {
        var = s;
        expression = e;
    }

    public String getVar() {
        return var;
    }

    @Override
    public void print(String x) {
        System.out.println(x + var);
        if (expression != null) {
            System.out.println(x + "[");
            expression.print(x + "    ");
            System.out.println(x + "]");
        }
    }
}
