
package compiler.compiler;
import lowlevel.*;
import compiler.parser.*;

public class CMinusCodeGen implements CodeGen {
    
    private Program p;
    
    public CMinusCodeGen(Program p){
        this.p = p;
    }
    
    public CodeItem genLLCode(){
        return p.genLLCode();
    }
}
