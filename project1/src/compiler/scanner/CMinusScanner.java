package compiler.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import compiler.scanner.State.StateType;

public class CMinusScanner implements Scanner {

	private BufferedReader inFile;
	private Token nextToken;
	private String[] keywords = {"else", "if", "int", "return", "void", "while"};
	
	public CMinusScanner(BufferedReader file){
		inFile = file;
		nextToken = scanToken();
	}
	private Token scanToken() {
		//index for storing into tokenString
		int tokenStringIndex = 0;
		
		//holds current token to be returned
		Token currentToken;
		
		//current sate- always begins at START
		StateType state = State.StateType.START;
		
		while (state != State.StateType.DONE) {
			//flag to indicate save to tokenString
			boolean save = true;
			
			char currentChar = getNextCharacter();

			switch (state) {
			case START:
				if (Character.isDigit(currentChar)) {
					state = State.StateType.NUM;
				}
				break;

			}
			switch (state) {
			case NUM:
				currentChar
				break;

			}
		}
		return null;
	}
	
	private char getNextCharacter(){
		char nextChar = 0;
		try {
			nextChar = (char) inFile.read();
		} catch (IOException e) {
			System.out.println("Scanner Exception: Unable to get next character.");
			e.printStackTrace();
		}
		return nextChar;
		
	}
	
	private char viewNextCharacter(){
		char nextChar = 0;
		try {
			inFile.mark(1);
			nextChar = (char) inFile.read();
			inFile.reset();
		} catch (IOException e) {
			System.out.println("Scanner Exception: Unable to View Next Character");
			e.printStackTrace();
		}
		return nextChar;
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
	
	public static void main(String[] args) {
		
		//input file from argument and create new FileReader
		File file = new File(args[0]);
		FileReader fileread = null;
		try {
			fileread = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.out.println("Scanner Exception: Unable to read file.");
			e.printStackTrace();
		}
		
		//declare BufferedReader
		BufferedReader br = new BufferedReader(fileread);
		
		//Create new CMinus Scanner
		CMinusScanner cMinusScan = new CMinusScanner(br);
	}

}
