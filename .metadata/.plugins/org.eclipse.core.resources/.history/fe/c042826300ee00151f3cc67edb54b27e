package compiler.parser;

import compiler.scanner.Token;

public class Factor {

	private Expression parseFactor (Parser parser) {
	    switch (parser.getCurrentToken().getTokenType()) {
	        case LEFTPAREN_TOKEN:
	            parser.advanceToken();
	            Expression returnExpr = parseExpression();
	            matchToken(Token.RPAREN_TOKEN);
	            return returnExpr;
	            break;
	        case Token.IDENT_TOKEN:
	            Token oldToken = advanceToken();
	            return createIdentExpr(oldToken);
	            break;
	        case Token.NUM_TOKEN:
	           Token oldToken = advanceToken();
	            return createNumExpr(oldToken);
	            break;
	        default:
	            logParseError();
	            return null;
	    }
	}
	
}
