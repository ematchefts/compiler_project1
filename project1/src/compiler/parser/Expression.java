package compiler.parser;

public abstract class Expression {
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

		switch 

	}
}
