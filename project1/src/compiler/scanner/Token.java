package compiler.scanner;

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

	// Constructors
	public Token(TokenType type) {
		this(type, null);
	}

	public Token(TokenType type, Object data) {
		tokenType = type;
		tokenData = data;
	}

	// Getters
	public TokenType getTokenType() {
		return tokenType;
	}

	public Object getTokenData() {
		return tokenData;
	}

	// Setters
	public void setTokenType(TokenType type) {
		tokenType = type;
	}

	public void setTokenData(Object data) {
		tokenData = data;
	}

}
