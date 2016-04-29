package compiler.parser;

public class ReturnStatement extends Statement {

    private Expression expression;

    public ReturnStatement(Expression e) {
        expression = e;
    }

    @Override
    public void print(String x) {
        System.out.println(x + "return: ");
        if (expression != null) {
            expression.print(x + "    ");
        } else {
            System.out.println(x + "    void");;
        }
    }
}
