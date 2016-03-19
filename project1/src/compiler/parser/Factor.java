package compiler.parser;

import compiler.scanner.Token;

public class Factor {
	
	private Expression expression;
	private VarCall varcall;
	private String id;
	private Integer num;
	
	public Factor (Expression exp){
		expression = exp;
	}
	
	public Factor (String ident, VarCall vc){
		id = ident;
		varcall = vc;
	}
	
	public Factor (Integer n){
		num = n;
	}

	public static Factor parseFactor(Parser parser) 
			throws ParserException{
		
		Factor factor;
		Token currentToken;
		
		switch(parser.getCurrentToken().getTokenType()){
			case LEFTPAREN_TOKEN:
				parser.advanceToken();
				Expression exp = Expression.parseExpression();
				parser.matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
				factor = new Factor (exp);
				return factor;
			case ID_TOKEN:
				currentToken = parser.getNextToken();
				String ident = (String) currentToken.getTokenData();
				parser.advanceToken();
				VarCall vc = VarCall.parseVarCall();
				factor = new Factor (ident, vc);
				return factor;
			case NUM_TOKEN:
				currentToken = parser.getNextToken();
				factor = new Factor ((Integer) currentToken.getTokenData());
				return factor;
			default:
				throw new ParserException("Parse exception occured while parsing factor");
		}
		
	}

}
