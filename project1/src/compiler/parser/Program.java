package compiler.parser;

import java.util.*;
import lowlevel.*;

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
    
    public CodeItem genLLCode() {

        if (declList.isEmpty()) {
            throw new CodeGenerationException("Empty decl list.");
        }

        CodeItem head = declList.get(0).genCode();
        CodeItem ci = head;

        for (int i = 1; i < declList.size(); i++) {
            ci.setNextItem(declList.get(i).genCode());
            ci = ci.getNextItem();
        }
        
        return head;
    }
}
