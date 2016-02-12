package compiler.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import compiler.scanner.State.StateType;

public class CMinusScanner implements Scanner {

	private BufferedReader inFile;
	private Token nextToken;
	private static final Map<String, State.StateType> keywordMap = new HashMap<String, State.StateType>();
	static {
		keywordMap.put("else", State.StateType.ELSE);
		keywordMap.put("if", State.StateType.IF);
		keywordMap.put("int", State.StateType.INT);
		keywordMap.put("return", State.StateType.RETURN);
		keywordMap.put("void", State.StateType.VOID);
		keywordMap.put("while", State.StateType.WHILE);
	}
	
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
				//change!
				else{
					state = State.StateType.DONE;
					switch(currentChar){
					case ',':
						currentToken = new Token(Token.TokenType.COMMA_TOKEN);
						break;
					case '{':
						currentToken = new Token(Token.TokenType.LEFTCURLYBRACE_TOKEN);
						break;
					case '(':
						currentToken = new Token(Token.TokenType.LEFTPAREN_TOKEN);
						break;
					case '[':
						currentToken = new Token(Token.TokenType.LEFTSQBRACKET_TOKEN);
						break;
					case '-':
						currentToken = new Token(Token.TokenType.MINUS_TOKEN);
						break;
					case '*':
						currentToken = new Token(Token.TokenType.MULTIPLY_TOKEN);
						break;
					case '+':
						currentToken = new Token(Token.TokenType.PLUS_TOKEN);
						break;
					case '}':
						currentToken = new Token(Token.TokenType.RIGHTCURLYBRACE_TOKEN);
						break;
					case ')':
						currentToken = new Token(Token.TokenType.RIGHTPAREN_TOKEN);
						break;
					case ']':
						currentToken = new Token(Token.TokenType.RIGHTSQBRACKET_TOKEN);
						break;
					case ';':
						currentToken = new Token(Token.TokenType.SEMICOLON_TOKEN);
						break;
					
					default:
						// Something's wrong - should never get here! Need to note an error here.
						break;
					}
					break;
				}
			case ASSIGN:
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
			case LESSTHAN:
				break;
			case LESSTHANEQUALTO:
				break;
			case NOTEQUAL:
				break;
			case NUM:
				break;
			case RETURN:
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
