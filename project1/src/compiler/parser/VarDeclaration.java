package compiler.parser;

import compiler.scanner.*;

public class VarDeclaration extends Declarations {
    private Integer optional;
    
    public VarDeclaration(String s, Integer num){
        super(Token.TokenType.INT_TOKEN, s);
        optional = num;
    }
    
    public VarDeclaration(String s){
        super(Token.TokenType.INT_TOKEN, s);
        this.optional = null;
    }
    
    @Override
    public void print(String x){
        if(optional == null){
            System.out.println(x + "int " + id + ";");
        }else{
            System.out.println(x + "int " + id + "[" + optional + "];");
        }
    }
}
