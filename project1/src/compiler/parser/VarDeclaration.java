package compiler.parser;

import compiler.scanner.*;
import lowlevel.*;

/**
 * This class is a subclass of Declarations.
 * It contains variable declarations
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class VarDeclaration extends Declarations {
    private Integer arraySize;
    
    /**
     * Variable declaration constructor
     * 
     * @param s The name of the variable declaration
     * @param num The size of the array in the declaration
     */
    public VarDeclaration(String s, Integer num){
        super(Token.TokenType.INT_TOKEN, s);
        arraySize = num;
    }
    
    /**
     * Variable declaration constructor
     * 
     * @param sThe name of the variable declaration
     */
    public VarDeclaration(String s){
        super(Token.TokenType.INT_TOKEN, s);
        this.arraySize = null;
    }
    
    @Override
    /**
     * Print function for variable declaration
     */
    public void print(String x){
        if(arraySize == null){
            System.out.println(x + "int " + id + ";");
        }else{
            System.out.println(x + "int " + id + "[" + arraySize + "];");
        }
    }
    
    @Override
    public CodeItem genCode(){
        int type;
        if(typeSpecification == Token.TokenType.INT_TOKEN){
            type = Data.TYPE_INT;
        }else{
            type = Data.TYPE_VOID;
        }
        if(arraySize == null){
            return new Data(type, id);
        }else{
            return new Data(type, id, true, arraySize);
        }
    }
}
