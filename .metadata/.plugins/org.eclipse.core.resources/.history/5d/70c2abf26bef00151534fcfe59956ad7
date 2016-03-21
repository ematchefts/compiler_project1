package compiler.parser;

import java.util.ArrayList;

import compiler.scanner.Token;

public class Args {
	ArrayList<Expression> exprList = new ArrayList<Expression>();
	
	public Args(ArrayList <Expression> el){
		exprList = el;
	}
	
	public static Args parseArg(Parser parser) throws ParserException{
		Expression exp = Expression.parseExpression(parser);
		ArrayList<Expression> el = new ArrayList<Expression>();
		el.add(exp);
		
		Token nextToken = parser.viewNextToken();
		while (nextToken.getTokenType() == Token.TokenType.COMMA_TOKEN){
			parser.advanceToken();
			exp = Expression.parseExpression(parser);
			el.add(exp);
		}
		
		Args args = new Args(el);
		return args;
		
	}
}
