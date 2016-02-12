package compiler.scanner;

public class State {
	
	//TODO: Decide if this is the best implementation of this
	public enum StateType {
		START,
		ASSIGN,
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
		LESSTHAN,
		LESSTHANEQUALTO,
		NOTEQUAL,
		NUM,
		RETURN,
		VOID,
		WHILE,
		DONE
	}

}
