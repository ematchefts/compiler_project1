package compiler.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;

import compiler.scanner.State.StateType;

public class CMinusScanner implements Scanner {

	private BufferedReader inFile;
	private Token nextToken;
	private boolean lastCapturedWasAlphaNum;
	
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
		lastCapturedWasAlphaNum = false;
		nextToken = scanToken();
	}
	
	private Token scanToken() {		
		//holds current token to be returned
		Token currentToken = null;
		
		//current sate- always begins at START
		StateType state = State.StateType.START;
		
		char currentChar = 0;
		char nextChar = 0;
		
		while (state != State.StateType.DONE && state != State.StateType.ERROR) {			
			currentChar = getNextCharacter();
			nextChar = viewNextCharacter();

			switch (state) {
			case START:
				if (Character.isWhitespace(currentChar)){
					this.lastCapturedWasAlphaNum = false;
					break;
				}
				else if (Character.isDigit(currentChar)) {
					
					if(!this.lastCapturedWasAlphaNum){
						currentToken = new Token(Token.TokenType.NUM_TOKEN, Character.getNumericValue(currentChar));
						this.lastCapturedWasAlphaNum = true;
						
						//Check if next token is also digit - if so, enter NUM state, else enter DONE
						if(Character.isDigit(nextChar)){
							state = State.StateType.NUM;
						}
						else{
							state = State.StateType.DONE;
						}
					}
					else{
						// TODO: lexical error!
					}
				}
				else if (Character.isLetter(currentChar)){
					
					if(!this.lastCapturedWasAlphaNum){
						currentToken = new Token(Token.TokenType.ID_TOKEN, Character.toString(currentChar));
						this.lastCapturedWasAlphaNum = true;
						
						//Check if next token is also digit - if so, enter NUM state, else enter DONE
						if(Character.isLetter(nextChar)){
							state = State.StateType.ID;
						}
						else{
							state = State.StateType.DONE;
						}
					}
					else{
						// TODO: lexical error!
					}
				}
				
				// Special-char tokens:
				else{
					this.lastCapturedWasAlphaNum = false;
					switch(currentChar){
					// COMMENT
					case '/':
						if(nextChar == '*'){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							state = State.StateType.COMMENT;
						}
						else{
							currentToken = new Token(Token.TokenType.DIVIDE_TOKEN);
							state = State.StateType.DONE;
						}
						break;
						
					// EQUALTO
					case '=':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.EQUALTO_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							currentToken = new Token(Token.TokenType.ASSIGN_TOKEN);
							state = State.StateType.DONE;
						}
						break;
					
					// NOTEQUAL
					case '!':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.NOTEQUAL_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							state = State.StateType.ERROR;
						}
						break;
					
					// LESSTHANEQUALTO
					case '<':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.LESSTHANEQUALTO_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							currentToken = new Token(Token.TokenType.LESSTHAN_TOKEN);
							state = State.StateType.DONE;
						}
						break;
						
					// GREATERTHANEQUALTO
					case '>':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.GREATERTHANEQUALTO_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							currentToken = new Token(Token.TokenType.GREATERTHAN_TOKEN);
							state = State.StateType.DONE;
						}
						break;
						
					// Single-char tokens
					default:
						// Because these are single-char tokens, we can assume we're DONE
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
							// TODO: Need to note an error here.
							break;
						}
					}	
				}
				break;
			case COMMENT:
				if(nextChar == '*'){
					currentChar = getNextCharacter();
					nextChar = viewNextCharacter();
					if(nextChar == '/'){
						currentChar = getNextCharacter();
						nextChar = viewNextCharacter();
						state = State.StateType.DONE;
					}
				}
				break;
			case DONE:
				break;
			case ID:
				// Add the current character to the Token's numeric value
				currentToken.setTokenData( (String)currentToken.getTokenData() + currentChar);
				if( !Character.isLetter(nextChar) ){
					// If next char isn't digit, we're done!
					this.lastCapturedWasAlphaNum = true;
					state = State.StateType.DONE;
				}
				break;
			case NUM:
				// Add the current character to the Token's numeric value
				currentToken.setTokenData( ( (int)(currentToken.getTokenData() ) * 10) + Character.getNumericValue(currentChar));
				if( !Character.isDigit(nextChar) ){
					// If next char isn't digit, we're done!
					this.lastCapturedWasAlphaNum = true;
					state = State.StateType.DONE;
				}
				break;
			default:
				break;
			// TODO: Something probably happens here.
			}
		}
		
		if(state == State.StateType.ERROR){
			System.out.println("Lexical error: " + currentChar + "followed by " + nextChar);
		}
		
		// We've hit the DONE (or ERROR) state, so reset to START and return the Token we got
		state = State.StateType.START;
		
		// But first, if we say we got an ID, check against keywords.
		
		return currentToken;
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
		nextToken =  returnToken;
		return scanToken();
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
		
		//declare our Token, used for writing out to lex results file
		Token writeToken = cMinusScan.viewNextToken();

		//Get ready to output a lex file
		try {
			PrintWriter writer;
			//Create the file
			writer = new PrintWriter(args[0] + ".lex", "UTF-8");
			
			//Print the first token as a line
			writer.println(writeToken.getTokenType().toString() + writeToken.getTokenData().toString());
			
			//Print all additional tokens as additional lines
			while(br.ready()){
				writeToken = cMinusScan.getNextToken();
				writer.println(writeToken.getTokenType().toString() + writeToken.getTokenData().toString());
			}
			
			//Close the writer
			writer.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		catch (IOException e){
			System.out.println("Scanner Exception: An IOException occurred while reading the input file.");
			e.printStackTrace();
		}
	}

}
