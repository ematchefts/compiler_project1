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
	
	public Token getCurrentToken(){
		return currentToken;
	}
	
	public static void main(String args[]){
		
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
			String tokenType = cMinusScan.getTokenTypeString();
			String tokenData = cMinusScan.getTokenDataString();
		}
		
		catch (FileNotFoundException e1) {
			System.out.println("Error: file not found");
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			System.out.println("Error: Unsupported file encoding");
			e1.printStackTrace();
		}

		catch (IOException e) {
			System.out.println("Scanner Exception: An IOException " + "occurred while reading the input file.");
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	void printAST(AbstractSyntaxTree ast){
		
	}
}
