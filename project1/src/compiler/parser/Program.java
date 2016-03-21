package compiler.parser;

import java.util.*;

public class Program {

    private ArrayList<Declarations> declList;

    public Program(ArrayList<Declarations> a) {
        declList = a;
    }

    public void print() {
        String x = "    ";
        System.out.println("program:");
        while (!declList.isEmpty()) {
            declList.remove(0).print(x);
        }
    }
}
