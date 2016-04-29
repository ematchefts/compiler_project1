package compiler.parser;
import lowlevel.*;

/**
* This class is an abstract declarations class.
* 
* @author Elizabeth Matchefts & Benjamin Seymour
*
*/
public abstract class Expression {
	protected int regNum;

	/**
	 * Abstract print class for expression
	 * 
	 * @param w the amount of whitespace to be printed
	 */
    public abstract void print(String w);
    
    public abstract void genCode(Function f);

    public int getRegNum() {
        return regNum;
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }
}
