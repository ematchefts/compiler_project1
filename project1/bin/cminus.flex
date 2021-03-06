/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 1998-2015  Gerwin Klein <lsf@jflex.de>                    *
 * All rights reserved.                                                    *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* Java 1.2 language lexer specification */

/* Use together with unicode.flex for Unicode preprocesssing */
/* and java12.cup for a Java 1.2 parser                      */

/* Note that this lexer specification is not tuned for speed.
   It is in fact quite slow on integer and floating point literals, 
   because the input is read twice and the methods used to parse
   the numbers are not very fast. 
   For a production quality application (e.g. a Java compiler) 
   this could be optimized */


package compiler.scanner;
import java.io.IOException;

%%

%public
%class CMinusScanner_jflex
%implements Scanner

%unicode

%line
%column

%function scanToken
%type Token

%initthrow IOException
%yylexthrow ScannerException

%{
  StringBuilder string = new StringBuilder();
  private Token nextToken;
  
  	public void setNextToken(Token token){
  		nextToken = token;
  	}
  
  	@Override
	public Token getNextToken(){
		Token returnToken = nextToken;
		try {
			if(nextToken.getTokenType() != Token.TokenType.EOF_TOKEN){
				nextToken = scanToken();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ScannerException s) {
			System.out.println(s.getMessage());
			s.printStackTrace();
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
	
	public Token.TokenType getTokenType(){
		Token.TokenType returnType = Token.TokenType.EOF_TOKEN;
		if (viewNextToken().getTokenType() != null) {
			returnType = viewNextToken().getTokenType();
		}
		return returnType;
	}
	
	public Object getTokenData() {
		Object returnData = null;
		if (viewNextToken().getTokenData() != null) {
			returnData = viewNextToken().getTokenData();
		}
		return returnData;
	}
	
	public int getCurrentLine(){
		return yyline + 1;
	}
	
	public int getCurrentColumn(){
		return yycolumn;
	}
	

%}

/* main character classes */
LineTerminator = \r|\n|\r\n

WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment}

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"

/* identifiers */
Identifier = [:jletter:]+

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*

/* Lexical errors*/
LexError = ([:jletter:][0-9]) | ([0-9][:jletter])


%state STRING, CHARLITERAL

%%

<YYINITIAL> {

  /* keywords */
  "while"                        { return new Token(Token.TokenType.WHILE_TOKEN); }
  "else"                         { return new Token(Token.TokenType.ELSE_TOKEN); }
  "int"                          { return new Token(Token.TokenType.INT_TOKEN); }
  "if"                           { return new Token(Token.TokenType.IF_TOKEN); }
  "return"                       { return new Token(Token.TokenType.RETURN_TOKEN); }
  "void"                         { return new Token(Token.TokenType.VOID_TOKEN); }
  
  /* separators */
  "("                            { return new Token(Token.TokenType.LEFTPAREN_TOKEN); }
  ")"                            { return new Token(Token.TokenType.RIGHTPAREN_TOKEN); }
  "{"                            { return new Token(Token.TokenType.LEFTCURLYBRACE_TOKEN); }
  "}"                            { return new Token(Token.TokenType.RIGHTCURLYBRACE_TOKEN); }
  "["                            { return new Token(Token.TokenType.LEFTSQBRACKET_TOKEN); }
  "]"                            { return new Token(Token.TokenType.RIGHTSQBRACKET_TOKEN); }
  ";"                            { return new Token(Token.TokenType.SEMICOLON_TOKEN); }
  ","                            { return new Token(Token.TokenType.COMMA_TOKEN); }
  
  /* operators */
  "="                            { return new Token(Token.TokenType.ASSIGN_TOKEN); }
  ">"                            { return new Token(Token.TokenType.GREATERTHAN_TOKEN); }
  "<"                            { return new Token(Token.TokenType.LESSTHAN_TOKEN); }
  "=="                           { return new Token(Token.TokenType.EQUALTO_TOKEN); }
  "<="                           { return new Token(Token.TokenType.LESSTHANEQUALTO_TOKEN); }
  ">="                           { return new Token(Token.TokenType.GREATERTHANEQUALTO_TOKEN); }
  "!="                           { return new Token(Token.TokenType.NOTEQUAL_TOKEN); }
  "+"                            { return new Token(Token.TokenType.PLUS_TOKEN); }
  "-"                            { return new Token(Token.TokenType.MINUS_TOKEN); }
  "*"                            { return new Token(Token.TokenType.MULTIPLY_TOKEN); }
  "/"                            { return new Token(Token.TokenType.DIVIDE_TOKEN); }

  /* numeric literals */

  /* This is matched together with the minus, because the number is too big to 
     be represented by a positive integer. */
  "-2147483648"                  { return new Token(Token.TokenType.NUM_TOKEN, new Integer(Integer.MIN_VALUE)); }
  
  {DecIntegerLiteral}            { return new Token(Token.TokenType.NUM_TOKEN, new Integer(yytext())); }
 
  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */ 
  {Identifier}                   { return new Token(Token.TokenType.ID_TOKEN, yytext()); }  
  
  /* Lexical Error */
  {LexError}                     {throw new ScannerException("Lexical error: Cannot have one IDENTIFIER, "+ "NUMBER, or keyword after another without " + "delimiters in between!"); }
}

/* error fallback */
[^]                              { throw new RuntimeException("Illegal character \""+yytext()+
                                                              "\" at line "+yyline+", column "+yycolumn); }
<<EOF>>                          { return new Token(Token.TokenType.EOF_TOKEN); }