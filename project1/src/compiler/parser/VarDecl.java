package compiler.parser;

import java.util.ArrayList;

public class VarDecl extends Decl {
	
	private String id;
	private ArrayList<Integer> dimensions;
	
	VarDecl(String identifier){
		id = identifier;
	}
	
	VarDecl(String identifier, ArrayList<Integer> dims){
		id = identifier;
		dimensions = dims;
	}
	
	
	public static VarDecl parseVarDecl(Parser parser){
		
	}
}
