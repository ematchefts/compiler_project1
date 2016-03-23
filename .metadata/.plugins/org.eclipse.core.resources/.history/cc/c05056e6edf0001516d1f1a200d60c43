package compiler.parser;

import compiler.scanner.*;

public class VarDeclaration extends Declarations {
    private Integer arraySize;
    
    public VarDeclaration(String s, Integer num){
        super(Token.TokenType.INT_TOKEN, s);
        arraySize = num;
    }
    
    public VarDeclaration(String s){
        super(Token.TokenType.INT_TOKEN, s);
        this.arraySize = null;
    }
    
    @Override
    public void print(String x){
        if(arraySize == null){
            System.out.println(x + "int " + id + ";");
        }else{
            System.out.println(x + "int " + id + "[" + arraySize + "];");
        }
    }
}
