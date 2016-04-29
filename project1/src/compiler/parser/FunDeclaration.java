package compiler.parser;

import java.util.ArrayList;
import lowlevel.*;
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
    
    public CodeItem genCode() {
        Function f;
        int type;
        if (typeSpecification == Token.TokenType.INT_TOKEN) {
            type = Data.TYPE_INT;
        } else {
            type = Data.TYPE_VOID;
        }
        if (params.isEmpty()) {
            f = new Function(type, id);
        } else {
            Param first = params.get(0);
            FuncParam firstParam = new FuncParam(Data.TYPE_INT, first.getId(), first.hasBrackets());
            FuncParam previousParam = firstParam;
            for (int i = 1; i < params.size(); i++) {
                Param next = params.get(i);
                FuncParam nextParam = new FuncParam(Data.TYPE_INT, next.getId(), next.hasBrackets());
                previousParam.setNextParam(nextParam);
                previousParam = nextParam;
            }
            f = new Function(type, id, firstParam);
            for(Param p: params){
                p.genCode(f);
            }
        }
        f.createBlock0();
        BasicBlock newBlock = new BasicBlock(f);
        f.appendBlock(newBlock);
        f.setCurrBlock(newBlock);
        compoundStatement.genCode(f);
        f.appendToCurrentBlock(f.getReturnBlock());
        //f.appendBlock(f.getReturnBlock());
        if (f.getFirstUnconnectedBlock() != null) {
            f.appendBlock(f.getFirstUnconnectedBlock());
        }
        return f;
    }
}
