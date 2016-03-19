package compiler.parser;

import compiler.scanner.Token;

public class ParserException extends Exception {
	ParserException(String message){
		super(message);
	}
	
	ParserException(Token.TokenType expected, Token.TokenType found){
		super( "Expected " + expected.toString() + 
		" but found " + found.toString() );
	}
	
	ParserException(Token.TokenType[] expected, Token.TokenType found){
		super( "Expected one of " + typeArrayToString(expected) + 
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
