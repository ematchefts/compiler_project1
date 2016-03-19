package compiler.parser;

import compiler.scanner.Token;

public abstract class Decl {
	
	public static Decl parseDecl(Parser parser) throws ParserException{
		Token.TokenType type = parser.getCurrentToken().getTokenType();
		switch(type){
		case INT_TOKEN:
			return new VarDecl();
		case VOID_TOKEN:
			return new FunDecl();
		default:
			throw new ParserException();
		}

	}
	
}
