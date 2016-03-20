package compiler.parser;

import compiler.scanner.Token;

public class Expression {
	private Integer num;
	private SimpleExpressionP simpleExpressionP;
	private Expression expression;
	private ExpressionP expressionP;
	private String id;
	
	public Expression(Integer n, SimpleExpressionP sep){
		num = n;
		simpleExpressionP = sep;
	}
	
	public Expression(Expression e, SimpleExpressionP sep){
		expression = e;
		simpleExpressionP = sep;
	}
	
	public Expression (String ident, ExpressionP ep){
		id = ident;
		expressionP = ep;
	}
	
	public static Expression parseExpression(Parser parser) 
			throws ParserException{
		
		Token currentToken = parser.getCurrentToken();
		Expression expression;
		SimpleExpressionP sep;
		
		switch(currentToken.getTokenType()){
			case NUM_TOKEN:
				Integer n = (Integer) currentToken.getTokenData();
				sep = SimpleExpressionP.parseSimpleExpresionP(parser);
				expression = new Expression (n, sep);
				return expression;
			case LEFTPAREN_TOKEN:
				Expression innerExpression = Expression.parseExpression(parser);
				sep = SimpleExpressionP.parseSimpleExpresionP(parser);
				expression = new Expression (innerExpression, sep);
				return expression;
			case ID_TOKEN:
				currentToken = parser.getCurrentToken();
				String ident = (String) currentToken.getTokenData();
				ExpressionP ep = ExpressionP.parseExpressionP(parser);
				expression = new Expression (ident, ep);
				return expression;
			default:
				throw new ParserException("Parse exception occured while parsing expression'");
				
		}
	}
}
