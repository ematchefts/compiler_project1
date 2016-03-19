package compiler.parser;

import compiler.scanner.Token;

public class Factor {
	
	private Expression expression;
	private VarCall varcall;
	private Integer num;
	
	public Factor (Expression exp){
		expression = exp;
	}
	
	public Factor (VarCall vc){
		varcall = vc;
	}
	
	public Factor (Integer n){
		num = n;
	}

	public static Factor parseFactor(Parser parser) 
			throws ParserException{
		
		Factor factor;
		
		switch(parser.getCurrentToken().getTokenType()){
			case LEFTPAREN_TOKEN:
				parser.advanceToken();
				Expression exp = Expression.parseExpression();
				parser.matchToken(Token.TokenType.RIGHTPAREN_TOKEN);
				factor = new Factor (exp);
				return factor;
			case ID_TOKEN:
				parser.advanceToken();
				VarCall vc = VarCall.parseVarCall();
				factor = new Factor (vc);
				return factor;
			case NUM_TOKEN:
				Token currentToken = parser.getNextToken();
				factor = new Factor ((Integer) currentToken.getTokenData());
				return factor;
			default:
				throw new ParserException("Parse exception occured while parsing factor");
		}
		
	}

}
