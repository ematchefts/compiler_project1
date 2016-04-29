package compiler.parser;

public class ExpressionStatement extends Statement {

    private Expression expression;

    public ExpressionStatement(Expression e) {
        expression = e;
    }

    @Override
    public void print(String w) {
        System.out.println(w + "expr stmt: ");
        if (expression != null) {
            expression.print(w + "    ");
        }
    }
}
