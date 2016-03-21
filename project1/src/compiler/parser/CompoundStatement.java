package compiler.parser;

import java.util.ArrayList;


public class CompoundStatement extends Statement {

    private ArrayList<VarDeclaration> varDeclarations;
    private ArrayList<Statement> statements;

    public CompoundStatement(ArrayList<VarDeclaration> v, ArrayList<Statement> s) {
        varDeclarations = v;
        statements = s;
    }

    @Override
    public void print(String w) {
        System.out.println(w + "{");
        if (!varDeclarations.isEmpty()) {
            System.out.println(w + "    " + "local decls:");
            while (!varDeclarations.isEmpty()) {
            	varDeclarations.remove(0).print(w + "        ");
            }
        }
        if (!statements.isEmpty()) {
            System.out.println(w + "    " + "statements:");
            while (!statements.isEmpty()) {
                statements.remove(0).print(w + "        ");
            }
        }
        System.out.println(w + "}");
    }
}
