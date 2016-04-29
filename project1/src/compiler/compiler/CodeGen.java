package compiler.compiler;
import lowlevel.*;

public interface CodeGen {
    public abstract CodeItem genLLCode();
}
