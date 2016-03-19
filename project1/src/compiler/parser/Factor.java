package compiler.parser;

import compiler.scanner.Token;

public class Factor {
	
	private Expression expression;
	private VarCall varcall;
	private Token num;
	
	public Factor (Expression exp){
		expression = exp;
	}
	
	public Factor (VarCall vc){
		varcall = vc;
	}
	
	public Factor (Token n){
		num = n;
	}

	public static Factor parseFactor(Parser parser) 
			throws ParserException{
		
		switch(parser.getCurrentToken().getTokenType()){
			case LEFTPAREN_TOKEN:
				parser.advanceToken();
				Expression exp = Expression.parseExpression();
		
		}
		return null;
		
	}

}
