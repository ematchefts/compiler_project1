package compiler.parser;

import java.util.ArrayList;

import compiler.scanner.*;

/**
 * This class is a subclass of Declaration.
 * It contains function declarations
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class FunDeclaration extends Declarations {

    private ArrayList<Param> params;
    private CompoundStatement compoundStatement;

    /**
     * Function declaration constructor
     * 
     * @param typeSpecification the return type of the function
     * @param id the name of the function
     * @param ps array list of function parameters
     * @param cs the compound statement inside the function
     */
    public FunDeclaration(Token.TokenType typeSpecification, String id, ArrayList<Param> ps, CompoundStatement cs) {
        super(typeSpecification, id);
        params = ps;
        compoundStatement = cs;
    }

    /**
     * Print function for function declaration
     */
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
