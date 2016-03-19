package compiler.parser;

import java.util.ArrayList;

import compiler.scanner.Token;

public class Args {
	ArrayList<Expression> exprList = new ArrayList<Expression>();
	
	public Args(ArrayList <Expression> el){
		exprList = el;
	}
	
	public static Args parseArg(Parser parser) {
		Expression exp = Expression.parseExpression();
		ArrayList<Expression> el = new ArrayList<Expression>();
		el.add(exp);
		
		Token nextToken = parser.viewNextToken();
		while (nextToken.getTokenType() == Token.TokenType.COMMA_TOKEN){
			exp = Expression.parseExpression();
			el.add(exp);
		}
		
		Args args = new Args(el);
		return args;
		
	}
}
