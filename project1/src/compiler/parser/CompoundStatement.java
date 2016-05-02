package compiler.parser;

import java.util.ArrayList;
import lowlevel.*;

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
            for (VarDeclaration vardecl : varDeclarations) {
            	vardecl.print(w + "        ");
            }
        }
        if (!statements.isEmpty()) {
            System.out.println(w + "    " + "statements:");
            for (Statement statement : statements) {
                statement.print(w + "        ");
            }
        }
        System.out.println(w + "}");
    }
    
    @Override
    public void genCode(Function f) {

        for (int i = 0; i < varDeclarations.size(); i++) {
            VarDeclaration v = varDeclarations.get(i);
            Data d = (Data) v.genCode();

            //Add to symbol table
            if (f.getTable().containsKey(v.id)) {
                throw new CodeGenerationException("Variable " + v.id + " was declared multiple times.");
            }
            
        }

        for (int i = 0; i < statements.size(); i++) {
            Statement s = statements.get(i);
            s.genCode(f);
        }

    }
}
