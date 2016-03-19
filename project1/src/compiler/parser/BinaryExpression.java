package compiler.parser;

import compiler.scanner.Token;

public class BinaryExpression {
	private Expression leftSide;
	private Expression rightSide;
	private Token.TokenType operation;
	
	public BinaryExpression (Expression ls, Expression rs, Token.TokenType op){
		leftSide = ls;
		rightSide = rs;
		operation = op;
	}
}
