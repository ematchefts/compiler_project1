package compiler.scanner;

/**
 * The token class defines a token found by the scanner. It includes the token type
 * and data found in the token.
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class Token {

	// tokenType enum
	public enum TokenType {
		ASSIGN_TOKEN, // '='
		COMMA_TOKEN,
		COMMENTSTART_TOKEN,
		COMMENTEND_TOKEN,
		DIVIDE_TOKEN,
		ELSE_TOKEN,
		EOF_TOKEN,
		EQUALTO_TOKEN, // '=='
		GREATERTHAN_TOKEN, 
		GREATERTHANEQUALTO_TOKEN,
		ID_TOKEN, 
		IF_TOKEN, 
		INT_TOKEN, 
		LEFTCURLYBRACE_TOKEN, 
		LEFTPAREN_TOKEN, 
		LEFTSQBRACKET_TOKEN, 
		LESSTHAN_TOKEN, 
		LESSTHANEQUALTO_TOKEN, 
		MINUS_TOKEN, 
		MULTIPLY_TOKEN, 
		NOTEQUAL_TOKEN, 
		NUM_TOKEN,
		PLUS_TOKEN, 
		RETURN_TOKEN, 
		RIGHTCURLYBRACE_TOKEN,
		RIGHTPAREN_TOKEN,
		RIGHTSQBRACKET_TOKEN, 
		SEMICOLON_TOKEN, 
		VOID_TOKEN, 
		WHILE_TOKEN
	}

	private TokenType tokenType;
	private Object tokenData;

	/**
	 * Token constructor
	 * 
	 * @param type the TokenType type of the token
	 */
	public Token(TokenType type) {
		this(type, null);
	}

	/**
	 * Token Constructor
	 * @param type the TokenType type of the token
	 * @param data the value found in the token
	 */
	public Token(TokenType type, Object data) {
		tokenType = type;
		tokenData = data;
	}

	/**
	 * Token type getter
	 * @return tokenType the TokenType type of the token
	 */
	public TokenType getTokenType() {
		return tokenType;
	}

	/**
	 * Token data getter
	 * @return tokenData the value found in the token
	 */
	public Object getTokenData() {
		return tokenData;
	}

	/**
	 * Token type setter
	 * @param type the TokenType type of the token
	 */
	public void setTokenType(TokenType type) {
		tokenType = type;
	}

	/**
	 * Token data setter
	 * @param data the value of the data found in the token
	 */
	public void setTokenData(Object data) {
		tokenData = data;
	}

}
