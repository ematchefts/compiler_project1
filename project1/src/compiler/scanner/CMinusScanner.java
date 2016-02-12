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
				if (Character.isWhitespace(currentChar)){
					break;
				}
				else if (Character.isDigit(currentChar)) {
					state = State.StateType.NUM;
				}
				else if (Character.isLetter(currentChar)){
					state = State.StateType.ID;
				}
				else{
					switch(currentChar){
					case ',':
						state = State.StateType.COMMA;
						break;
					case '>':
						state = State.StateType.GREATERTHAN;
						break;
					case '{':
						state = State.StateType.LEFTCURLYBRACE;
						break;
					case '(':
						state = State.StateType.LEFTPAREN;
						break;
					case '[':
						state = State.StateType.LEFTSQBRACKET;
						break;
					case '<':
						state = State.StateType.LESSTHAN;
						break;
					case '-':
						state = State.StateType.MINUS;
						break;
					case '*':
						state = State.StateType.MULTIPLY;
						break;
					case '+':
						state = State.StateType.PLUS;
						break;
					case '}':
						state = State.StateType.RIGHTCURLYBRACE;
						break;
					case ')':
						state = State.StateType.RIGHTPAREN;
						break;
					case ']':
						state = State.StateType.RIGHTSQBRACKET;
						break;
					case ';':
						state = State.StateType.SEMICOLON;
						break;
					
					default:
						// Something's wrong - should never get here! Need to note an error here.
						break;
					}
					break;
				}
			case ASSIGN:
				break;
			case COMMA:
				break;
			case COMMENTEND:
				break;
			case COMMENTSTART:
				break;
			case DIVIDE:
				break;
			case DONE:
				break;
			case ELSE:
				break;
			case EQUALTO:
				break;
			case GREATERTHAN:
				break;
			case GREATERTHANEQUALTO:
				break;
			case ID:
				break;
			case IF:
				break;
			case INT:
				break;
			case LEFTCURLYBRACE:
				break;
			case LEFTPAREN:
				break;
			case LEFTSQBRACKET:
				break;
			case LESSTHAN:
				break;
			case LESSTHANEQUALTO:
				break;
			case MINUS:
				break;
			case MULTIPLY:
				break;
			case NOTEQUAL:
				break;
			case NUM:
				break;
			case PLUS:
				break;
			case RETURN:
				break;
			case RIGHTCURLYBRACE:
				break;
			case RIGHTPAREN:
				break;
			case RIGHTSQBRACKET:
				break;
			case SEMICOLON:
				break;
			case VOID:
				break;
			case WHILE:
				break;
			default:
				break;
			
			}
			
			currentChar = viewNextCharacter();
			
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
