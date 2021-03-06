
package compiler.parser;
import compiler.scanner.*;

 /**
 * This class is an abstract declarations class.
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public abstract class Declarations {
    protected Token.TokenType typeSpecification;
    protected String id;   
    
    /**
     * Declarations constructor
     * 
     * @param ts the type specifier of the declaration
     * @param s the name of the declaration
     */
    public Declarations(Token.TokenType ts, String s){
        typeSpecification = ts;
        id = s;
    }
    
    /**
     * Abstract print function for declaration
     */
    public abstract void print(String w);
}
