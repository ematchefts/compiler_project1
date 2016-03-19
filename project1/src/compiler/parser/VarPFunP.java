package compiler.parser;

import java.util.ArrayList;

import compiler.scanner.Token;

public class VarPFunP {

	private ArrayList<?> list;
	private CompoundStatement cs;
	
	VarPFunP(ArrayList<Integer> numbers){
		list = numbers;
	}
	
	VarPFunP(ArrayList<Param> params, CompoundStatement csgo){
		list = params;
		cs = csgo;
	}

	
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
				parser.matchToken(Token.TokenType.SEMICOLON_TOKEN);
				return new VarPFunP(dims);
			}
			else{
				throw new ParserException(Token.TokenType.NUM_TOKEN,
						parser.viewNextToken().getTokenType());
			}
		case LEFTPAREN_TOKEN:
			ArrayList<Param> params = new ArrayList<Param>();
			while(parser.viewNextToken().getTokenType() == Token.TokenType.INT_TOKEN){
				params.add(Param.parseParam(parser));
			}
			return new VarPFunP(params, CompoundStatement.parseCompoundStatement(parser));
			
		default:
			Token.TokenType[] types = {
					Token.TokenType.SEMICOLON_TOKEN, 
					Token.TokenType.LEFTPAREN_TOKEN, 
					Token.TokenType.LEFTSQBRACKET_TOKEN};
			throw new ParserException(types, type);
		}
		
	}
}
