package compiler.parser;

import java.util.ArrayList;

public class CompoundStatement {

	private ArrayList<Decl> declarations;
	private ArrayList<Statement> statements;

	CompoundStatement(ArrayList<Decl> decls, ArrayList<Statement> stmts) {
		declarations = decls;
		statements = stmts;
	}
	
	public static CompoundStatement parseCompoundStatement(Parser parser){
		return CompoundStatement(null, null);
	}

}
