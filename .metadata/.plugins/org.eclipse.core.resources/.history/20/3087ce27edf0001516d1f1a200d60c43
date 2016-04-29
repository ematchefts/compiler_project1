package compiler.parser;

import compiler.scanner.Token;

public class ParserException extends Exception {

	/**
     * Creates a new instance of <code>ParseException</code> without detail
     * message.
     */
    public ParserException() {
    }

    /**
     * Constructs an instance of <code>ParseException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ParserException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>ParseException</code> with a message 
     * listing the expected TokenType and the found TokenType
     * 
     * @param expected
     * @param found
     */
    ParserException(Token.TokenType expected, Token.TokenType found){
		super( "Expected " + expected.toString() + 
		" but found " + found.toString() );
	}
    
    /**
     * Constructs an instance of <code>ParseException</code> with a message 
     * listing members of an array of expected TokenTypes and the found 
     * TokenType
     * 
     * @param expected
     * @param found
     */
	ParserException(Token.TokenType[] expected, Token.TokenType found){
		super( "Expected one of " + typeArrayToString(expected) + 
				" but found " + found.toString() );
	}
	
	/**
     * Constructs an instance of <code>ParseException</code> with a message 
     * listing the expected TokenType and the found TokenType, and the name of 
     * the class that was in the middle of being parsed
     * 
     * @param expected
     * @param found
     */
    ParserException(Token.TokenType expected, Token.TokenType found, Class parsingClass){
		super( "Parsing " + parsingClass.getName() + ": Expected " + expected.toString() + 
		" but found " + found.toString() );
	}

    /**
     * Constructs an instance of <code>ParseException</code> with a message 
     * listing members of an array of expected TokenTypes and the found 
     * TokenType, and the name of the class that was in the middle of being 
     * parsed
     * 
     * @param expected
     * @param found
     */
	ParserException(Token.TokenType[] expected, Token.TokenType found, Class parsingClass){
		super( "Parsing " + parsingClass.getName() + ": Expected one of " + typeArrayToString(expected) + 
				" but found " + found.toString() );
	}
	
	
	private static String typeArrayToString(Token.TokenType[] types){
		String typesString = "";
		for(Token.TokenType type : types){
			typesString += (type.toString() + ",");
		}
		return typesString;
	}
}
