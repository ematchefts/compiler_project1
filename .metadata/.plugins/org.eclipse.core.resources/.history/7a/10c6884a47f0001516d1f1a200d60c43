package compiler.parser;

public class BinaryExpression extends Expression {

    public enum operation {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        EQUALTO,
        NOTEQUAL,
        LESSTHAN,
        GREATERTHAN,
        LESSTHANEQUALTO,
        GREATERTHANEQUALTO
    }

    private Expression operand1;
    private Expression operand2;
    private operation op;

    public BinaryExpression(Expression operand1, Expression operand2, operation op) {
        operand1 = operand1;
        operand2 = operand2;
        op = op;
    }

    @Override
    public void print(String x) {
        System.out.println(x + op.toString());
        operand1.print(x + "    ");
        operand2.print(x + "    ");
    }
}
