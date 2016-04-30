package compiler.compiler;

import java.io.*;

import compiler.parser.ParserException;

public interface Compiler {
  public void compile(String filePrefix) throws IOException, ParserException;
}
