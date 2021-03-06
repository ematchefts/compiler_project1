package compiler.parser;

import java.util.ArrayList;

/**
 * This class is a subclass of statement.
 * It contains compound statements
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class CompoundStatement extends Statement {

    private ArrayList<VarDeclaration> varDeclarations;
    private ArrayList<Statement> statements;

    /**
     * Compound Statement constructor
     * 
     * @param v an array list of variable declarations
     * @param s an array list of statements
     */
    public CompoundStatement(ArrayList<VarDeclaration> v, ArrayList<Statement> s) {
        varDeclarations = v;
        statements = s;
    }

    @Override
    /**
     * This function prints a compound statement in an abstract syntax tree
     */
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
