package compiler.parser;

import java.util.ArrayList;

import compiler.scanner.Token;

public class VarPFunP {

	private ArrayList<Param> params;
	private ArrayList<Integer> dimensions;
	
	public static VarPFunP parseVarPFunP(Parser parser) 
			throws ParserException{
		
		Token.TokenType type = parser.getNextToken().getTokenType();
		switch(type){
		case SEMICOLON_TOKEN:
			return null;
		case LEFTSQBRACKET_TOKEN:
			if(parser.viewNextToken().getTokenType() == Token.TokenType.NUM_TOKEN){
				ArrayList<Integer> dims = new ArrayList<Integer>();
				dims.add( (Integer) parser.getNextToken().getTokenData());
				
				while(parser.getNextToken().getTokenType() == Token.TokenType.COMMA_TOKEN){
					dims.add( (Integer) parser.getNextToken().getTokenData());
				}
				
				parser.matchToken(Token.TokenType.RIGHTSQBRACKET_TOKEN);
			}
			else{
				throw new ParserException("Expected NUM_TOKEN but found " + 
						parser.viewNextToken().getTokenType().toString());
			}
		}
		
		return new VarPFunP();
	}
}
