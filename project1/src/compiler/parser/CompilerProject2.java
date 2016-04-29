package compiler.parser;

import java.io.*;
import compiler.scanner.*;

public class CompilerProject2 {

    /**
     * This function opens a file and buffered stream for the scanner to red the input code file, then 
     * calls the parser constructor to parse the code.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        System.out.println("What file would you like to parse?");
        java.util.Scanner inputScanner = new java.util.Scanner(System.in);
        String input = inputScanner.nextLine();

        FileReader inputFile = new FileReader(input);

        System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("ast.txt")), true));        
        
        CMinusScanner_jflex myScan = new CMinusScanner_jflex(inputFile);
        Parser myParser = new CMinusParser(myScan);
        Program myProgram;
        
        try {
            myProgram = myParser.parse();
            myProgram.print();
        } catch (ParserException pe) {
        	System.err.println("Parser hit exception and stopped reading the "
        			+ "input file on line " + myScan.getCurrentLine() + 
        			", somewhere around column " + myScan.getCurrentColumn() + ".");
            pe.printStackTrace(); // Includes exception message
        }
        
        inputScanner.close();
    }
}
