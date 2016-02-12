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
	
	private static final Map<String, Token.TokenType> keywordMap = 
			new HashMap<String, Token.TokenType>();
	static {
		keywordMap.put("else", Token.TokenType.ELSE_TOKEN);
		keywordMap.put("if", Token.TokenType.IF_TOKEN);
		keywordMap.put("int", Token.TokenType.INT_TOKEN);
		keywordMap.put("return", Token.TokenType.RETURN_TOKEN);
		keywordMap.put("void", Token.TokenType.VOID_TOKEN);
		keywordMap.put("while", Token.TokenType.WHILE_TOKEN);
	}
	
	public CMinusScanner(BufferedReader file) throws ScannerException{
		inFile = file;
		lastCapturedWasAlphaNum = false;
		nextToken = scanToken();
	}
	
	private Token scanToken() throws ScannerException {
		//holds current token to be returned
		Token currentToken = null;
		
		//current sate- always begins at START
		StateType state = State.StateType.START;
		
		char currentChar = 0;
		char nextChar = 0;
		
		while(state != State.StateType.DONE){
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
						currentToken = new Token(Token.TokenType.NUM_TOKEN, 
								Character.getNumericValue(currentChar));
						this.lastCapturedWasAlphaNum = true;
						
						//Check if next token is also digit - 
						//if so, enter NUM state, else enter DONE
						if(Character.isDigit(nextChar)){
							state = State.StateType.NUM;
						}
						else{
							state = State.StateType.DONE;
						}
					}
					else{
						throw new ScannerException(
								"Lexical error: Cannot have one IDENTIFIER, "
								+ "NUMBER, or keyword after another without "
								+ "delimiters in between!");
					}
				}
				else if (Character.isLetter(currentChar)){
					
					if(!this.lastCapturedWasAlphaNum){
						currentToken = new Token(Token.TokenType.ID_TOKEN, 
								Character.toString(currentChar));
						this.lastCapturedWasAlphaNum = true;
						
						//Check if next token is also digit - 
						//if so, enter NUM state, else enter DONE
						if(Character.isLetter(nextChar)){
							state = State.StateType.ID;
						}
						else{
							state = State.StateType.DONE;
						}
					}
					else{
						throw new ScannerException(
								"Lexical error: Cannot have one IDENTIFIER, "
								+ "NUMBER, or keyword after another without "
								+ "delimiters in between!");
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
							currentToken = new Token(
									Token.TokenType.DIVIDE_TOKEN);
							state = State.StateType.DONE;
						}
						break;
						
					// EQUALTO
					case '=':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(
									Token.TokenType.EQUALTO_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							currentToken = new Token(
									Token.TokenType.ASSIGN_TOKEN);
							state = State.StateType.DONE;
						}
						break;
					
					// NOTEQUAL
					case '!':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(
									Token.TokenType.NOTEQUAL_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							throw new ScannerException(
									"Lexical error: Cannot use '!' character "
									+ "unless in '!=' or inside a comment");
						}
						break;
					
					// LESSTHANEQUALTO
					case '<':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(
									Token.TokenType.LESSTHANEQUALTO_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							currentToken = new Token(
									Token.TokenType.LESSTHAN_TOKEN);
							state = State.StateType.DONE;
						}
						break;
						
					// GREATERTHANEQUALTO
					case '>':
						if(nextChar == '='){
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(
									Token.TokenType.GREATERTHANEQUALTO_TOKEN);
							state = State.StateType.DONE;
						}
						else{
							currentToken = new Token(
									Token.TokenType.GREATERTHAN_TOKEN);
							state = State.StateType.DONE;
						}
						break;
						
					// Single-char tokens
					default:
						// Because these are single-char tokens, 
						// we can assume we're in DONE state
						state = State.StateType.DONE;
						switch(currentChar){
						case ',':
							currentToken = new Token(
									Token.TokenType.COMMA_TOKEN);
							break;
						case '{':
							currentToken = new Token(
									Token.TokenType.LEFTCURLYBRACE_TOKEN);
							break;
						case '(':
							currentToken = new Token(
									Token.TokenType.LEFTPAREN_TOKEN);
							break;
						case '[':
							currentToken = new Token(
									Token.TokenType.LEFTSQBRACKET_TOKEN);
							break;
						case '-':
							currentToken = new Token(
									Token.TokenType.MINUS_TOKEN);
							break;
						case '*':
							currentToken = new Token(
									Token.TokenType.MULTIPLY_TOKEN);
							break;
						case '+':
							currentToken = new Token(
									Token.TokenType.PLUS_TOKEN);
							break;
						case '}':
							currentToken = new Token(
									Token.TokenType.RIGHTCURLYBRACE_TOKEN);
							break;
						case ')':
							currentToken = new Token(
									Token.TokenType.RIGHTPAREN_TOKEN);
							break;
						case ']':
							currentToken = new Token(
									Token.TokenType.RIGHTSQBRACKET_TOKEN);
							break;
						case ';':
							currentToken = new Token(
									Token.TokenType.SEMICOLON_TOKEN);
							break;
						
						default:
							throw new ScannerException(
									"Lexical error: the CMinusScanner cannot "
									+ "accept the character '" + currentChar
									+ "' unless inside a comment");
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
						// Reached end of comment - we don't want to store it 
						// at all, so reset to START
						state = State.StateType.START;
					}
				}
				break;
			case DONE:
				break;
			case ID:
				// Add the current character to the Token's numeric value
				currentToken.setTokenData( 
						(String)currentToken.getTokenData() + currentChar);
				if( !Character.isLetter(nextChar) ){
					// If next char isn't digit, we're done!
					this.lastCapturedWasAlphaNum = true;
					state = State.StateType.DONE;
				}
				break;
			case NUM:
				// Add the current character to the Token's numeric value
				currentToken.setTokenData( 
						((int)(currentToken.getTokenData() ) * 10) +
						Character.getNumericValue(currentChar));
				if( !Character.isDigit(nextChar) ){
					// If next char isn't digit, we're done!
					this.lastCapturedWasAlphaNum = true;
					state = State.StateType.DONE;
				}
				break;
			default:
				throw new ScannerException(
						"Scanner state error: Scanner entered an invalid "
						+ "state.");
			}
		}
		
		// We've hit the DONE state, so reset to START 
		// and return the Token we got
		state = State.StateType.START;
		
		// But first, if we say we got an ID, check against keywords.
		if(currentToken.getTokenType() == Token.TokenType.ID_TOKEN){
			String word = (String)currentToken.getTokenData();
			Token.TokenType newType = CMinusScanner.keywordMap.get(word);
			if(newType != null){
				currentToken.setTokenType(newType);
				currentToken.setTokenData(null);
			}
		}
		
		return currentToken;
	}
	
	private char getNextCharacter(){
		char nextChar = 0;
		try {
			nextChar = (char) inFile.read();
		} catch (IOException e) {
			System.out.println(
					"Scanner Exception: Unable to get next character.");
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
			System.out.println(
					"Scanner Exception: Unable to View Next Character");
			e.printStackTrace();
		}
		return nextChar;
	}
	
	@Override
	public Token getNextToken() {
		try{
			nextToken = scanToken();
		}
		catch(ScannerException e){
			System.out.println(e.getMessage());
		}
		return nextToken;
	}

	@Override
	public Token viewLastToken() {
		return nextToken;
	}
	
	public String getTokenTypeString(){
		String returnString = "";
		if(viewLastToken().getTokenType() != null){
			returnString = viewLastToken().getTokenType().toString();
		}
		return returnString;
	}
	
	public String getTokenDataString(){
		String returnString = "";
		if(viewLastToken().getTokenData() != null){
			returnString = viewLastToken().getTokenData().toString();
		}
		return returnString;
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
		
		

		//Get ready to scan and output a lex file
		try {
			//Create new CMinus Scanner
			CMinusScanner cMinusScan = new CMinusScanner(br);
			
			//Get strings from the initial captured token
			String tokenType = cMinusScan.getTokenTypeString();
			String tokenData = cMinusScan.getTokenDataString();
			
			PrintWriter writer;
			//Create the file
			writer = new PrintWriter(args[0] + ".lex", "UTF-8");
			
			//Print the first token as a line
			writer.println(tokenType + "			" + tokenData);
			
			//Print all additional tokens as additional lines
			while(br.ready()){
				cMinusScan.getNextToken();
				tokenType = cMinusScan.getTokenTypeString();
				tokenData = cMinusScan.getTokenDataString();
				writer.println(tokenType + "			" + tokenData);
			}
			
			//Close the writer
			writer.close();
			
		} catch (FileNotFoundException e1) {
			System.out.println("Error: file not found");
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Error: Unsupported file encoding");
			e1.printStackTrace();
		}
		
		catch (IOException e){
			System.out.println("Scanner Exception: An IOException "
					+ "occurred while reading the input file.");
			e.printStackTrace();
		} catch (ScannerException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
