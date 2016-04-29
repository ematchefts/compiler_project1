package compiler.parser;

import java.util.ArrayList;

public class CallExpression extends Expression {

    private String call;
    private ArrayList<Expression> args;

    public CallExpression(String c, ArrayList<Expression> a) {
        call = c;
        args = a;
    }

    @Override
    public void print(String x) {
        System.out.println(x + "call: " + call);
        System.out.println(x + "    " + "params:");
        while (!args.isEmpty()) {
            args.remove(0).print(x + "    ");
        }
    }
}
