package compiler.parser;

public class IfStatement extends Statement {

    private Expression expression;
    private Statement statement;
    private Statement elseStatement;

    public IfStatement(Expression e, Statement s, Statement es) {
        expression = e;
        statement = s;
        elseStatement = es;
    }

    @Override
    public void print(String w) {
        System.out.println(w + "if (");
        expression.print(w + "    ");
        System.out.print(w + ")\n" + w + "then:\n");
        statement.print(w + "    ");
        if (elseStatement != null) {
            System.out.println(w + "else:");
            elseStatement.print(w + "    ");
        }
    }
}
