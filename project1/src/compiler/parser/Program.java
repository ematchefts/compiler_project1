package compiler.parser;

import java.util.ArrayList;

import compiler.scanner.Token;

public class Program {
	
	public static ArrayList<Decl> parseProgram(Parser parser)
		throws ParserException{
		
		ArrayList<Decl> declList = new ArrayList<Decl>();
		Decl declaration = Decl.parseDecl(parser);
		declList.add(declaration);
		
		while(parser.getNextToken().getTokenType() != Token.TokenType.EOF_TOKEN){
			declaration = Decl.parseDecl(parser);
			declList.add(declaration);
		}
		
		return declList;
	}
}
