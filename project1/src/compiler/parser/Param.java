package compiler.parser;

/**
 * This class contains parameters
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class Param {

    private final String id;
    private final boolean hasBrackets;

    /**
     * Parameter constructor
     * 
     * @param ident the name of the parameter
     * @param brackets whether the parameter has brackets or not
     */
    public Param(String ident, boolean brackets) {
        id = ident;
        hasBrackets = brackets;
    }

    /**
     * Print function for parameters
     * @param w The whitespace to line up the abstract syntax tree
     */
    public void print(String w) {
        System.out.print(w + id);
        if (hasBrackets) {
            System.out.print("[]");
        }
    }
}
