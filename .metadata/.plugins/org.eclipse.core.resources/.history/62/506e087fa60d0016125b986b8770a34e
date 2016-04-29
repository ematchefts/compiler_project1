package compiler.parser;

import java.util.ArrayList;

/**
 * This class is a subclass of Expression.
 * It stores expressions of the form call(params)
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class CallExpression extends Expression {

    private String call;
    private ArrayList<Expression> args;

    /**
     * Call Expression constructor
     * 
     * @param c The call of the call expression
     * @param a An array list of the arguments in the call parameters
     */
    public CallExpression(String c, ArrayList<Expression> a) {
        call = c;
        args = a;
    }

    /**
     * This function prints a call expression.
     */
    @Override
    public void print(String x) {
        System.out.println(x + "call: " + call);
        System.out.println(x + "    " + "params:");
        while (!args.isEmpty()) {
            args.remove(0).print(x + "    ");
        }
    }
}
