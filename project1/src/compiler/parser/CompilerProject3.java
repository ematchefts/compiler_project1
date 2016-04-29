package compiler.parser;

import java.io.*;
import java.util.*;
import compiler.scanner.*;


public class CompilerProject3 {

    /**
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
            System.err.println(pe.getMessage());
        }
        
        inputFile = new FileReader("ast.txt");
        
    }
}
