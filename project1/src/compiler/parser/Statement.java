package compiler.parser;
import lowlevel.*;

/**
 * Abstract statement class
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public abstract class Statement {

	/**
	 * Abstract print function for statements
	 * 
	 * @param x white space, used to line up abstract syntax tree
	 */
    public abstract void print(String x);
    
    public abstract void genCode(Function f);
}
