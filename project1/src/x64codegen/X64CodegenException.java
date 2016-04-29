package x64codegen;

import x86codegen.*;

public class X64CodegenException extends RuntimeException{

  public X64CodegenException(String msg) {
    super (msg);
  }
}