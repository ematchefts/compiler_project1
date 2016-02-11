package compiler.scanner;

public class State {
	
	//TODO: Decide if this is the best implementation of this
	public enum StateType {
		START,
		ASSIGN,
		COMMA,
		COMMENTSTART,
		COMMENTEND,
		DIVIDE,
		ELSE,
		EQUALTO,
		GREATERTHAN,
		GREATERTHANEQUALTO,
		ID,
		IF,
		INT,
		LEFTCURLYBRACE,
		LEFTPAREN,
		LEFTSQBRACKET,
		LESSTHAN,
		LESSTHANEQUALTO,
		MINUS,
		MULTIPLY,
		NOTEQUAL,
		NUM,
		PLUS,
		RETURN,
		RIGHTCURLYBRACE,
		RIGHTPAREN,
		RIGHTSQBRACKET,
		SEMICOLON,
		VOID,
		WHILE,
		DONE
	}

}
