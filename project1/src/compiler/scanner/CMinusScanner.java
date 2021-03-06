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

/**
 * This class is a C Minus implementation of a scanner. 
 * @author Elizabeth Matchefts & Ben Seymour
 *
 */
public class CMinusScanner implements Scanner {

	private BufferedReader inFile;
	private Token nextToken;
	private boolean lastCapturedWasAlphaNum;

	//Create a hashmap of key words to use to compare to ID tokens after ID tokens are identified
	private static final Map<String, Token.TokenType> keywordMap = new HashMap<String, Token.TokenType>();

	static {
		keywordMap.put("else", Token.TokenType.ELSE_TOKEN);
		keywordMap.put("if", Token.TokenType.IF_TOKEN);
		keywordMap.put("int", Token.TokenType.INT_TOKEN);
		keywordMap.put("return", Token.TokenType.RETURN_TOKEN);
		keywordMap.put("void", Token.TokenType.VOID_TOKEN);
		keywordMap.put("while", Token.TokenType.WHILE_TOKEN);
	}

	/**
	 * C Minus Scanner Constructor
	 * @param file  The buffered reader of the input file containing the c-minus code to be scanned
	 * @throws ScannerException
	 */
	public CMinusScanner(BufferedReader file) throws ScannerException {
		inFile = file;
		lastCapturedWasAlphaNum = false;
		nextToken = scanToken();
	}

	/**
	 * This is the main method of the C Minus Scanner class. It reads through a token character by character and
	 * returns the character type
	 * @return currentToken The current token being read of the input C Minus file
	 * @throws ScannerException If a lexical error has occurred
	 */
	private Token scanToken() throws ScannerException {
		// holds current token to be returned
		Token currentToken = null;

		// current sate- always begins at START
		StateType state = State.StateType.START;

		char currentChar = 0;
		char nextChar = 0;

		while (state != State.StateType.DONE) {
			currentChar = getNextCharacter();
			nextChar = viewNextCharacter();

			switch (state) {
			case START:
				if (Character.isWhitespace(currentChar)) {
					this.lastCapturedWasAlphaNum = false;
					break;
				} else if (Character.isDigit(currentChar)) {

					if (!this.lastCapturedWasAlphaNum) {
						currentToken = new Token(Token.TokenType.NUM_TOKEN, Character.getNumericValue(currentChar));
						this.lastCapturedWasAlphaNum = true;

						// Check if next token is also digit -
						// if so, enter NUM state, else enter DONE
						if (Character.isDigit(nextChar)) {
							state = State.StateType.NUM;
						} else {
							state = State.StateType.DONE;
						}
					} else {
						throw new ScannerException("Lexical error: Cannot have one IDENTIFIER, "
								+ "NUMBER, or keyword after another without " + "delimiters in between!");
					}
				} else if (Character.isLetter(currentChar)) {

					if (!this.lastCapturedWasAlphaNum) {
						currentToken = new Token(Token.TokenType.ID_TOKEN, Character.toString(currentChar));
						this.lastCapturedWasAlphaNum = true;

						// Check if next token is also digit -
						// if so, enter NUM state, else enter DONE
						if (Character.isLetter(nextChar)) {
							state = State.StateType.ID;
						} else {
							state = State.StateType.DONE;
						}
					} else {
						throw new ScannerException("Lexical error: Cannot have one IDENTIFIER, "
								+ "NUMBER, or keyword after another without " + "delimiters in between!");
					}
				}

				// Special-char tokens:
				else {
					this.lastCapturedWasAlphaNum = false;
					switch (currentChar) {
					// COMMENT
					case '/':
						if (nextChar == '*') {
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							state = State.StateType.COMMENT;
						} else {
							currentToken = new Token(Token.TokenType.DIVIDE_TOKEN);
							state = State.StateType.DONE;
						}
						break;

					// EQUALTO
					case '=':
						if (nextChar == '=') {
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.EQUALTO_TOKEN);
			
						} else {
							currentToken = new Token(Token.TokenType.ASSIGN_TOKEN);
						}
						state = State.StateType.DONE;
						break;

					// NOTEQUAL
					case '!':
						if (nextChar == '=') {
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.NOTEQUAL_TOKEN);
							state = State.StateType.DONE;
						} else {
							throw new ScannerException(
									"Lexical error: Cannot use '!' character " + "unless in '!=' or inside a comment");
						}
						break;

					// LESSTHANEQUALTO
					case '<':
						if (nextChar == '=') {
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.LESSTHANEQUALTO_TOKEN);
						} else {
							currentToken = new Token(Token.TokenType.LESSTHAN_TOKEN);
						}
						state = State.StateType.DONE;
						break;

					// GREATERTHANEQUALTO
					case '>':
						if (nextChar == '=') {
							currentChar = getNextCharacter();
							nextChar = viewNextCharacter();
							currentToken = new Token(Token.TokenType.GREATERTHANEQUALTO_TOKEN);
						} else {
							currentToken = new Token(Token.TokenType.GREATERTHAN_TOKEN);
						}
						state = State.StateType.DONE;
						break;

					// Single-char tokens
					default:
						// Because these are single-char tokens,
						// we can assume we're in DONE state
						state = State.StateType.DONE;
						switch (currentChar) {
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
							throw new ScannerException("Lexical error: the CMinusScanner cannot "
									+ "accept the character '" + currentChar + "' unless inside a comment");
						}
					}
				}
				break;
			case COMMENT:
				if (nextChar == '*') {
					currentChar = getNextCharacter();
					nextChar = viewNextCharacter();
					if (nextChar == '/') {
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
				currentToken.setTokenData((String) currentToken.getTokenData() + currentChar);
				if (!Character.isLetter(nextChar)) {
					// If next char isn't digit, we're done!
					this.lastCapturedWasAlphaNum = true;
					state = State.StateType.DONE;
				}
				break;
			case NUM:
				// Add the current character to the Token's numeric value
				currentToken.setTokenData(
						((int) (currentToken.getTokenData()) * 10) + Character.getNumericValue(currentChar));
				if (!Character.isDigit(nextChar)) {
					// If next char isn't digit, we're done!
					this.lastCapturedWasAlphaNum = true;
					state = State.StateType.DONE;
				}
				break;
			default:
				throw new ScannerException("Scanner state error: Scanner entered an invalid " + "state.");
			}
		}

		// We've hit the DONE state, so reset to START
		// and return the Token we got
		state = State.StateType.START;

		// But first, if we say we got an ID, check against keywords.
		if (currentToken.getTokenType() == Token.TokenType.ID_TOKEN) {
			String word = (String) currentToken.getTokenData();
			Token.TokenType newType = CMinusScanner.keywordMap.get(word);
			if (newType != null) {
				currentToken.setTokenType(newType);
				currentToken.setTokenData(null);
			}
		}

		return currentToken;
	}

	/**
	 * This function gets the next character in the input file. It "munches the character"
	 * so that the next time this function is called a new next character will be returned.
	 * 
	 * @return nextChar The next character in the input file
	 */
	private char getNextCharacter() {
		char nextChar = 0;
		try {
			nextChar = (char) inFile.read();
		} catch (IOException e) {
			System.out.println("Scanner Exception: Unable to get next character.");
			e.printStackTrace();
		}
		return nextChar;

	}

	/**
	 * This function peeks ahead to the next character in the input file without "munching
	 * the character."
	 * 
	 * @return nextChar The next character in the input file
	 */
	private char viewNextCharacter() {
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

	/**
	 * This function returns the next token in the input file. It "munches the token"
	 * so that the next time this function is called a new next token will be returned.
	 * 
	 * @return nextToken The next token in the input file
	 */
	@Override
	public Token getNextToken() {
		Token returnToken = nextToken;
		try {
			nextToken = scanToken();
		} catch (ScannerException e) {
			System.out.println(e.getMessage());
		}
		return returnToken;
	}

	/**
	 * This function peeks ahead to the next token in the input file without "munching
	 * the token."
	 * 
	 * @return nextToken The next token in the input file
	 */
	@Override
	public Token viewNextToken() {
		return nextToken;
	}

	/**
	 * This function returns the string type value of the last token read
	 * 
	 * @return returnString The string value of the last token read
	 */
	public String getTokenTypeString() {
		String returnString = "";
		if (viewNextToken().getTokenType() != null) {
			returnString = viewNextToken().getTokenType().toString();
		}
		return returnString;
	}

	/**
	 * This function returns the string data value of the last token read
	 * 
	 * @return returnstring the string value of the last token read
	 */
	public String getTokenDataString() {
		String returnString = "";
		if (viewNextToken().getTokenData() != null) {
			returnString = viewNextToken().getTokenData().toString();
		}
		return returnString;
	}
	
	/**
	 * The main function reads in the file and gets the next token in the file from the 
	 * scanner until the end of the file is reached
	 * 
	 * @param args[0] The path to the file to be read
	 */
	public static void main(String[] args) {

		// input file from argument and create new FileReader
		File file = new File(args[0]);
		FileReader fileread = null;
		try {
			fileread = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.out.println("Scanner Exception: Unable to read file.");
			e.printStackTrace();
		}

		// declare BufferedReader
		BufferedReader br = new BufferedReader(fileread);

		// Get ready to scan and output a lex file
		try {
			// Create new CMinus Scanner
			CMinusScanner cMinusScan = new CMinusScanner(br);

			// Get strings from the initial captured token
			String tokenType = cMinusScan.getTokenTypeString();
			String tokenData = cMinusScan.getTokenDataString();

			PrintWriter writer;
			// Create the file
			writer = new PrintWriter(args[0] + ".lex", "UTF-8");

			// Print the first token as a line
			writer.println(tokenType + "			" + tokenData);

			// Print all additional tokens as additional lines
			while (br.ready()) {
				cMinusScan.getNextToken();
				tokenType = cMinusScan.getTokenTypeString();
				tokenData = cMinusScan.getTokenDataString();
				writer.println(tokenType + "			" + tokenData);
			}

			// Close the writer
			writer.close();

		} catch (FileNotFoundException e1) {
			System.out.println("Error: file not found");
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Error: Unsupported file encoding");
			e1.printStackTrace();
		}

		catch (IOException e) {
			System.out.println("Scanner Exception: An IOException " + "occurred while reading the input file.");
			e.printStackTrace();
		} catch (ScannerException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
