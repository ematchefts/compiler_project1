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
		if(parser.viewNextToken().getTokenType() == Token.TokenType.ID_TOKEN){
			
			String ident = (String) parser.getNextToken().getTokenData();
			boolean pIsArray = false;
			
			if(parser.viewNextToken().getTokenType() == Token.TokenType.LEFTSQBRACKET_TOKEN){
				parser.matchToken(Token.TokenType.RIGHTSQBRACKET_TOKEN);
				pIsArray = true;
			}
			
			return new Param(ident, pIsArray);
		}
		else{
			throw new ParserException(Token.TokenType.ID_TOKEN, parser.getCurrentToken().getTokenType());
		}
	}
}
