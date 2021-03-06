package compiler.parser;

import java.util.*;

/**
 * This class contains the entire program being parsed. 
 * It is the entry point for parsing.
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class Program {

    private ArrayList<Declarations> declList;

    /**
     * Program constructor
     * @param a an array list of declarations in the program
     */
    public Program(ArrayList<Declarations> a) {
        declList = a;
    }

    /**
     * Program print function
     */
    public void print() {
        String x = "    ";
        System.out.println("program:");
        while (!declList.isEmpty()) {
            declList.remove(0).print(x);
        }
    }
}
