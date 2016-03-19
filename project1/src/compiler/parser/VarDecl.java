package compiler.parser;

public class VarDecl extends Decl {
	
	VarDecl(String identifier, VarPFunP primer){
		super(identifier, primer);
	}
	
	public static VarDecl parseVarDecl(Parser parser) 
			throws ParserException{
		String id = (String) parser.getNextToken().getTokenData();
		return new VarDecl(id, VarPFunP.parseVarPFunP(parser));
	}
}
