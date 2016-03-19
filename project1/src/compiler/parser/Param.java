package compiler.parser;

import compiler.scanner.Token;

public class Param {

	private String id;
	private boolean isArray;
	
	Param(String ident, boolean pIsArray){
		id = ident;
		isArray = pIsArray;
	}
	
	public static Param parseParam(Parser parser) 
			throws ParserException{
		parser.matchToken(Token.TokenType.INT_TOKEN);
		if(parser.getNextToken().getTokenType() == Token.TokenType.ID_TOKEN){
			
		}
		else{
			throw new ParserException(Token.TokenType.ID_TOKEN, parser.getCurrentToken().getTokenType());
		}
	}
}
