package compiler.parser;

import java.util.ArrayList;

public class Program {
	
	public static ArrayList<Decl> parseProgram(Parser parser)
		throws ParserException{
		
		ArrayList<Decl> declList = new ArrayList<Decl>();
		Decl declaration = Decl.parseDecl(parser);
		declList.add(declaration);
		
		return declList;
	}
}
