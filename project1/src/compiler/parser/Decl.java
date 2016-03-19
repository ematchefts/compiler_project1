package compiler.parser;

import compiler.scanner.Token;

public class Decl {
	
	protected String id;
	private VarPFunP prime;
	
	
	Decl(String ident, VarPFunP primer){
		id = ident;
		prime = primer;
	}
	
	public static Decl parseDecl(Parser parser) throws ParserException{
		Token.TokenType type = parser.getCurrentToken().getTokenType();
		switch(type){
		case INT_TOKEN:
			return VarDecl.parseVarDecl(parser);
		case VOID_TOKEN:
			Token ident = parser.getNextToken();
			parser.advanceToken(); // to get initial token of a VarPFunP
			return new Decl(ident.toString(), VarPFunP.parseVarPFunP(parser));
		default:
			throw new ParserException();
		}

	}
	
}
