package compiler.parser;

import java.util.ArrayList;

import compiler.scanner.*;


public class FunDeclaration extends Declarations {

    private ArrayList<Param> params;
    private CompoundStatement compoundStatement;

    public FunDeclaration(Token.TokenType typeSpecification, String id, ArrayList<Param> ps, CompoundStatement cs) {
        super(typeSpecification, id);
        params = ps;
        compoundStatement = cs;
    }

    @Override
    public void print(String x) {
        System.out.println(x + "function " + id + " returns " + typeSpecification);
        System.out.println(x + "    " + "params:");
        while (!params.isEmpty()) {
            params.remove(0).print(x + "        ");
            System.out.println();
        }
        compoundStatement.print(x + "    ");
    }
}
