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
     * @throws ParserException 
     */
    public void print() throws ParserException  {
        String w = "    ";
        System.out.println("program:");
        if (declList.isEmpty()) {
            throw new ParserException("Empty decl list.");
        }

        for (Declarations declList1 : declList) {
            declList1.print(w);
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
