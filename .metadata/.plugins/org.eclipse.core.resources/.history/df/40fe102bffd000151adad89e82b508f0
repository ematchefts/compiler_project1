package compiler.scanner;

import java.io.BufferedReader;

public class CMinusScanner implements Scanner {

	private BufferedReader inFile;
	private Token nextToken;
	
	public CMinusScanner(BufferedReader file){
		inFile = file;
		nextToken = scanToken();
	}
	private Token scanToken() {
		// TODO Implement scanToken()
		return null;
	}
	@Override
	public Token getNextToken() {
		Token returnToken = nextToken;
		if(nextToken.getTokenType() != Token.TokenType.EOF_TOKEN){
			nextToken = scanToken();
		}
		return returnToken;
	}

	@Override
	public Token viewNextToken() {
		return nextToken;
	}

}
