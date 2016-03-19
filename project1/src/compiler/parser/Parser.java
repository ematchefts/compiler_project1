package compiler.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import compiler.scanner.*;

public class Parser {
	
	private Token currentToken;	
	private Scanner scanner;
	
	Parser(String filename){
		
		// input file from argument and create new FileReader
		File file = new File(filename);
		FileReader fileread = null;
		try {
			fileread = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Unable to read file.");
			e.printStackTrace();
		}

		// declare BufferedReader
		Reader br = new BufferedReader(fileread);

		// Get ready to scan and output a lex file
		try {
			// Create new CMinus Scanner
			CMinusScanner_jflex cMinusScan = new CMinusScanner_jflex(br);
			try {
				cMinusScan.setNextToken(cMinusScan.scanToken());
			} catch (ScannerException e) {
				e.printStackTrace();
			}

			// Get strings from the initial captured token
			currentToken.setTokenType(cMinusScan.getTokenType());
			currentToken.setTokenData(cMinusScan.getTokenData());
		}
		
		catch (FileNotFoundException e1) {
			System.out.println("Error: file not found");
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Error: Unsupported file encoding");
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("Scanner Exception: An IOException " 
					+ "occurred while reading the input file.");
			e.printStackTrace();
		}

	}
	
	public Token getCurrentToken(){
		return currentToken;
	}
	
	public void advanceToken(){
		getNextToken();
	}
	
	public Token getNextToken(){
		currentToken = scanner.getNextToken(); 
		return currentToken;
	}
	
	public Token viewNextToken(){
		return scanner.viewNextToken();
	}
	
	public void matchToken(Token.TokenType matchTokenType) throws ParserException{
		if (getNextToken().getTokenType() != matchTokenType){
			throw new ParserException("Expected " + matchTokenType.toString() + 
					" but found " + getCurrentToken().getTokenType().toString());
		}
	}

	public boolean hasNextToken(){
		return (viewNextToken().getTokenType() != Token.TokenType.EOF_TOKEN);
	}
	
	public static void main(String args[]){
		
		Parser parser = new Parser(args[0]);
		
	}
	
	
	
	
	
	void printAST(AbstractSyntaxTree ast){
		
	}
}
