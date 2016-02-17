package compiler.scanner;

/**
 * The Interface for a scanner. It will be implemented individually
 * for the language required.
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public interface Scanner {
	/**
	 * This function returns the next token in the input file. It "munches the token"
	 * so that the next time this function is called a new next token will be returned.
	 * 
	 * @return  The next token in the input file
	 */
	public Token getNextToken();
	
	/**
	 * This function peeks ahead to the next token in the input file without "munching
	 * the token."
	 * 
	 * @return nextToken The next token in the input file
	 */
	public Token viewLastToken();
}
