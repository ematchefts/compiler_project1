package compiler.parser;

/**
 * This class is a subclass of Expression.
 * It contains expressions of the form a operator b
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
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

    /**
     * Binary Expression constructor 
     * 
     * @param op1 expression of the first operand
     * @param op2 expression of the second operand
     * @param o The operation between the first and second operand
     */
    public BinaryExpression(Expression op1, Expression op2, operation o) {
        operand1 = op1;
        operand2 = op2;
        op = o;
    }

    /**
     * This function prints a Binary Expression in an abstract syntax tree
     */
    @Override
    public void print(String x) {
        System.out.println(x + op.toString());
        operand1.print(x + "    ");
        operand2.print(x + "    ");
    }
}
