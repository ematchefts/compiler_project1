package compiler.parser;

public class WhileStatement extends Statement {

    private Expression expression;
    private Statement statement;

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
