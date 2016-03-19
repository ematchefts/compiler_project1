package compiler.parser;

import java.util.ArrayList;

public class Program {
	
	public static ArrayList<Decl> parseProgram(){
		
		ArrayList<Decl> declList = new ArrayList<Decl>();
		Decl declaration = Decl.parseDecl();
		declList.add(declaration);
		
		return declList;
	}
}
