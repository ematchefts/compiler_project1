/* The following code was generated by JFlex 1.6.1 */

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


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.1
 * from the specification file <tt>C:/Users/Elizabeth/Documents/Spring 2016/Compiler/projects/project1/compiler_project1/project1/src/cminus.flex</tt>
 */
public class CMinusScanner_jflex implements Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int STRING = 2;
  public static final int CHARLITERAL = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\3\1\2\1\0\1\3\1\1\22\0\1\3\1\43\2\0"+
    "\1\6\3\0\1\31\1\32\1\5\1\44\1\37\1\45\1\0\1\4"+
    "\1\7\1\47\1\46\1\53\1\50\1\10\1\54\1\51\1\52\1\10"+
    "\1\11\1\36\1\42\1\40\1\41\2\0\32\6\1\34\1\0\1\35"+
    "\1\0\1\6\1\0\3\6\1\30\1\17\1\23\1\6\1\14\1\15"+
    "\1\12\1\6\1\16\1\6\1\21\1\27\2\6\1\24\1\20\1\22"+
    "\1\25\1\26\1\13\3\6\1\33\1\0\1\33\44\0\4\6\4\0"+
    "\1\6\12\0\1\6\4\0\1\6\5\0\27\6\1\0\37\6\1\0"+
    "\u01ca\6\4\0\14\6\16\0\5\6\7\0\1\6\1\0\1\6\201\0"+
    "\5\6\1\0\2\6\2\0\4\6\10\0\1\6\1\0\3\6\1\0"+
    "\1\6\1\0\24\6\1\0\123\6\1\0\213\6\10\0\236\6\11\0"+
    "\46\6\2\0\1\6\7\0\47\6\7\0\1\6\100\0\33\6\5\0"+
    "\3\6\30\0\1\6\24\0\53\6\43\0\2\6\1\0\143\6\1\0"+
    "\1\6\17\0\2\6\7\0\2\6\12\0\3\6\2\0\1\6\20\0"+
    "\1\6\1\0\36\6\35\0\131\6\13\0\1\6\30\0\41\6\11\0"+
    "\2\6\4\0\1\6\5\0\26\6\4\0\1\6\11\0\1\6\3\0"+
    "\1\6\27\0\31\6\107\0\1\6\1\0\13\6\127\0\66\6\3\0"+
    "\1\6\22\0\1\6\7\0\12\6\17\0\7\6\1\0\7\6\5\0"+
    "\10\6\2\0\2\6\2\0\26\6\1\0\7\6\1\0\1\6\3\0"+
    "\4\6\3\0\1\6\20\0\1\6\15\0\2\6\1\0\3\6\16\0"+
    "\4\6\7\0\1\6\11\0\6\6\4\0\2\6\2\0\26\6\1\0"+
    "\7\6\1\0\2\6\1\0\2\6\1\0\2\6\37\0\4\6\1\0"+
    "\1\6\23\0\3\6\20\0\11\6\1\0\3\6\1\0\26\6\1\0"+
    "\7\6\1\0\2\6\1\0\5\6\3\0\1\6\22\0\1\6\17\0"+
    "\2\6\17\0\1\6\23\0\10\6\2\0\2\6\2\0\26\6\1\0"+
    "\7\6\1\0\2\6\1\0\5\6\3\0\1\6\36\0\2\6\1\0"+
    "\3\6\17\0\1\6\21\0\1\6\1\0\6\6\3\0\3\6\1\0"+
    "\4\6\3\0\2\6\1\0\1\6\1\0\2\6\3\0\2\6\3\0"+
    "\3\6\3\0\14\6\26\0\1\6\50\0\1\6\13\0\10\6\1\0"+
    "\3\6\1\0\27\6\1\0\12\6\1\0\5\6\3\0\1\6\32\0"+
    "\2\6\6\0\2\6\43\0\10\6\1\0\3\6\1\0\27\6\1\0"+
    "\12\6\1\0\5\6\3\0\1\6\40\0\1\6\1\0\2\6\17\0"+
    "\2\6\22\0\10\6\1\0\3\6\1\0\51\6\2\0\1\6\20\0"+
    "\1\6\21\0\2\6\30\0\6\6\5\0\22\6\3\0\30\6\1\0"+
    "\11\6\1\0\1\6\2\0\7\6\72\0\60\6\1\0\2\6\13\0"+
    "\10\6\72\0\2\6\1\0\1\6\2\0\2\6\1\0\1\6\2\0"+
    "\1\6\6\0\4\6\1\0\7\6\1\0\3\6\1\0\1\6\1\0"+
    "\1\6\2\0\2\6\1\0\4\6\1\0\2\6\11\0\1\6\2\0"+
    "\5\6\1\0\1\6\25\0\4\6\40\0\1\6\77\0\10\6\1\0"+
    "\44\6\33\0\5\6\163\0\53\6\24\0\1\6\20\0\6\6\4\0"+
    "\4\6\3\0\1\6\3\0\2\6\7\0\3\6\4\0\15\6\14\0"+
    "\1\6\21\0\46\6\1\0\1\6\5\0\1\6\2\0\53\6\1\0"+
    "\u014d\6\1\0\4\6\2\0\7\6\1\0\1\6\1\0\4\6\2\0"+
    "\51\6\1\0\4\6\2\0\41\6\1\0\4\6\2\0\7\6\1\0"+
    "\1\6\1\0\4\6\2\0\17\6\1\0\71\6\1\0\4\6\2\0"+
    "\103\6\45\0\20\6\20\0\125\6\14\0\u026c\6\2\0\21\6\1\0"+
    "\32\6\5\0\113\6\3\0\3\6\17\0\15\6\1\0\4\6\16\0"+
    "\22\6\16\0\22\6\16\0\15\6\1\0\3\6\17\0\64\6\43\0"+
    "\1\6\3\0\2\6\103\0\130\6\10\0\51\6\1\0\1\6\5\0"+
    "\106\6\12\0\35\6\63\0\36\6\2\0\5\6\13\0\54\6\25\0"+
    "\7\6\70\0\27\6\11\0\65\6\122\0\1\6\135\0\57\6\21\0"+
    "\7\6\67\0\36\6\15\0\2\6\12\0\54\6\32\0\44\6\51\0"+
    "\3\6\12\0\44\6\153\0\4\6\1\0\4\6\3\0\2\6\11\0"+
    "\300\6\100\0\u0116\6\2\0\6\6\2\0\46\6\2\0\6\6\2\0"+
    "\10\6\1\0\1\6\1\0\1\6\1\0\1\6\1\0\37\6\2\0"+
    "\65\6\1\0\7\6\1\0\1\6\3\0\3\6\1\0\7\6\3\0"+
    "\4\6\2\0\6\6\4\0\15\6\5\0\3\6\1\0\7\6\102\0"+
    "\2\6\23\0\1\6\34\0\1\6\15\0\1\6\20\0\15\6\3\0"+
    "\33\6\107\0\1\6\4\0\1\6\2\0\12\6\1\0\1\6\3\0"+
    "\5\6\6\0\1\6\1\0\1\6\1\0\1\6\1\0\4\6\1\0"+
    "\13\6\2\0\4\6\5\0\5\6\4\0\1\6\21\0\51\6\u0a77\0"+
    "\57\6\1\0\57\6\1\0\205\6\6\0\4\6\3\0\2\6\14\0"+
    "\46\6\1\0\1\6\5\0\1\6\2\0\70\6\7\0\1\6\20\0"+
    "\27\6\11\0\7\6\1\0\7\6\1\0\7\6\1\0\7\6\1\0"+
    "\7\6\1\0\7\6\1\0\7\6\1\0\7\6\120\0\1\6\u01d5\0"+
    "\3\6\31\0\11\6\7\0\5\6\2\0\5\6\4\0\126\6\6\0"+
    "\3\6\1\0\132\6\1\0\4\6\5\0\51\6\3\0\136\6\21\0"+
    "\33\6\65\0\20\6\u0200\0\u19b6\6\112\0\u51cd\6\63\0\u048d\6\103\0"+
    "\56\6\2\0\u010d\6\3\0\20\6\12\0\2\6\24\0\57\6\20\0"+
    "\31\6\10\0\120\6\47\0\11\6\2\0\147\6\2\0\4\6\1\0"+
    "\4\6\14\0\13\6\115\0\12\6\1\0\3\6\1\0\4\6\1\0"+
    "\27\6\25\0\1\6\7\0\64\6\16\0\62\6\76\0\6\6\3\0"+
    "\1\6\16\0\34\6\12\0\27\6\31\0\35\6\7\0\57\6\34\0"+
    "\1\6\60\0\51\6\27\0\3\6\1\0\10\6\24\0\27\6\3\0"+
    "\1\6\5\0\60\6\1\0\1\6\3\0\2\6\2\0\5\6\2\0"+
    "\1\6\1\0\1\6\30\0\3\6\2\0\13\6\7\0\3\6\14\0"+
    "\6\6\2\0\6\6\2\0\6\6\11\0\7\6\1\0\7\6\221\0"+
    "\43\6\35\0\u2ba4\6\14\0\27\6\4\0\61\6\u2104\0\u016e\6\2\0"+
    "\152\6\46\0\7\6\14\0\5\6\5\0\1\6\1\0\12\6\1\0"+
    "\15\6\1\0\5\6\1\0\1\6\1\0\2\6\1\0\2\6\1\0"+
    "\154\6\41\0\u016b\6\22\0\100\6\2\0\66\6\50\0\15\6\66\0"+
    "\2\6\30\0\3\6\31\0\1\6\6\0\5\6\1\0\207\6\7\0"+
    "\1\6\34\0\32\6\4\0\1\6\1\0\32\6\13\0\131\6\3\0"+
    "\6\6\2\0\6\6\2\0\6\6\2\0\3\6\3\0\2\6\3\0"+
    "\2\6\31\0\14\6\1\0\32\6\1\0\23\6\1\0\2\6\1\0"+
    "\17\6\2\0\16\6\42\0\173\6\105\0\65\6\u010b\0\35\6\3\0"+
    "\61\6\57\0\37\6\21\0\33\6\65\0\36\6\2\0\44\6\4\0"+
    "\10\6\1\0\5\6\52\0\236\6\u0362\0\6\6\2\0\1\6\1\0"+
    "\54\6\1\0\2\6\3\0\1\6\2\0\27\6\252\0\26\6\12\0"+
    "\32\6\106\0\70\6\6\0\2\6\100\0\1\6\17\0\4\6\1\0"+
    "\3\6\1\0\33\6\54\0\35\6\203\0\66\6\12\0\26\6\12\0"+
    "\23\6\215\0\111\6\u03ba\0\65\6\113\0\55\6\40\0\31\6\32\0"+
    "\44\6\134\0\60\6\16\0\4\6\u04bb\0\53\6\u0955\0\u036f\6\221\0"+
    "\143\6\u0b9d\0\u042f\6\u33d1\0\u0239\6\u04c7\0\105\6\13\0\1\6\102\0"+
    "\15\6\u4060\0\2\6\u23fe\0\125\6\1\0\107\6\1\0\2\6\2\0"+
    "\1\6\2\0\2\6\2\0\4\6\1\0\14\6\1\0\1\6\1\0"+
    "\7\6\1\0\101\6\1\0\4\6\2\0\10\6\1\0\7\6\1\0"+
    "\34\6\1\0\4\6\1\0\5\6\1\0\1\6\3\0\7\6\1\0"+
    "\u0154\6\2\0\31\6\1\0\31\6\1\0\37\6\1\0\31\6\1\0"+
    "\37\6\1\0\31\6\1\0\37\6\1\0\31\6\1\0\37\6\1\0"+
    "\31\6\1\0\10\6\u1634\0\4\6\1\0\33\6\1\0\2\6\1\0"+
    "\1\6\2\0\1\6\1\0\12\6\1\0\4\6\1\0\1\6\1\0"+
    "\1\6\6\0\1\6\4\0\1\6\1\0\1\6\1\0\1\6\1\0"+
    "\3\6\1\0\2\6\1\0\1\6\2\0\1\6\1\0\1\6\1\0"+
    "\1\6\1\0\1\6\1\0\1\6\1\0\2\6\1\0\1\6\2\0"+
    "\4\6\1\0\7\6\1\0\4\6\1\0\4\6\1\0\1\6\1\0"+
    "\12\6\1\0\21\6\5\0\3\6\1\0\5\6\1\0\21\6\u1144\0"+
    "\ua6d7\6\51\0\u1035\6\13\0\336\6\u3fe2\0\u021e\6\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\u05f0\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\2\2\1\3\1\4\1\5\2\6\5\5"+
    "\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16"+
    "\1\17\1\20\1\1\1\21\1\22\1\0\1\5\1\23"+
    "\1\6\2\5\1\24\3\5\1\25\1\26\1\27\1\30"+
    "\3\0\1\5\1\31\3\5\2\0\1\5\1\32\1\5"+
    "\1\33\1\0\1\34\1\5\1\0\1\35\5\0\1\36";

  private static int [] zzUnpackAction() {
    int [] result = new int[67];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\55\0\132\0\207\0\132\0\264\0\132\0\341"+
    "\0\u010e\0\u013b\0\u0168\0\u0195\0\u01c2\0\u01ef\0\u021c\0\132"+
    "\0\132\0\132\0\132\0\132\0\132\0\132\0\u0249\0\u0276"+
    "\0\u02a3\0\u02d0\0\132\0\u02fd\0\u032a\0\u0357\0\132\0\u0384"+
    "\0\u03b1\0\u03de\0\u0357\0\u040b\0\u0438\0\u0465\0\132\0\132"+
    "\0\132\0\132\0\u0492\0\u04bf\0\u04ec\0\u0519\0\u0357\0\u0546"+
    "\0\u0573\0\u05a0\0\u05cd\0\u05fa\0\u0627\0\u0357\0\u0654\0\u0357"+
    "\0\u0681\0\u0357\0\u06ae\0\u06db\0\u0357\0\u0708\0\u0735\0\u0762"+
    "\0\u078f\0\u07bc\0\132";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[67];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\2\5\1\6\1\7\1\10\1\11\1\12"+
    "\1\3\1\10\1\13\1\10\1\14\1\10\1\15\4\10"+
    "\1\16\1\10\1\17\2\10\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
    "\1\34\7\12\55\3\57\0\1\5\57\0\1\35\55\0"+
    "\1\36\2\37\1\0\17\36\15\0\7\37\11\0\2\37"+
    "\3\0\2\37\2\0\1\37\1\0\1\37\37\0\2\40"+
    "\2\37\3\0\2\37\2\0\1\37\1\0\1\37\21\0"+
    "\7\40\6\0\1\36\2\37\1\0\2\36\1\41\14\36"+
    "\15\0\7\37\6\0\1\36\2\37\1\0\7\36\1\42"+
    "\1\36\1\43\5\36\15\0\7\37\6\0\1\36\2\37"+
    "\1\0\4\36\1\44\12\36\15\0\7\37\6\0\1\36"+
    "\2\37\1\0\5\36\1\45\11\36\15\0\7\37\6\0"+
    "\1\36\2\37\1\0\15\36\1\46\1\36\15\0\7\37"+
    "\40\0\1\47\54\0\1\50\54\0\1\51\54\0\1\52"+
    "\62\0\1\53\6\0\5\54\1\55\47\54\6\0\1\36"+
    "\3\0\17\36\33\0\2\40\35\0\7\40\6\0\1\36"+
    "\3\0\3\36\1\56\13\36\32\0\1\36\3\0\10\36"+
    "\1\57\6\36\32\0\1\36\3\0\6\36\1\60\10\36"+
    "\32\0\1\36\3\0\10\36\1\61\6\36\32\0\1\36"+
    "\3\0\3\36\1\62\13\36\73\0\1\63\5\0\5\54"+
    "\1\64\47\54\4\0\1\5\1\55\55\0\1\36\3\0"+
    "\4\36\1\65\12\36\32\0\1\36\3\0\5\36\1\66"+
    "\11\36\32\0\1\36\3\0\13\36\1\67\3\36\32\0"+
    "\1\36\3\0\16\36\1\70\74\0\1\71\4\0\4\54"+
    "\1\5\1\64\47\54\6\0\1\36\3\0\5\36\1\72"+
    "\11\36\32\0\1\36\3\0\12\36\1\73\4\36\75\0"+
    "\1\74\11\0\1\36\3\0\7\36\1\75\7\36\74\0"+
    "\1\76\56\0\1\77\55\0\1\100\55\0\1\101\50\0"+
    "\1\102\56\0\1\103\2\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[2025];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\1\1\1\11\1\1\1\11\10\1\7\11"+
    "\4\1\1\11\1\1\1\0\1\1\1\11\7\1\4\11"+
    "\3\0\5\1\2\0\4\1\1\0\2\1\1\0\1\1"+
    "\5\0\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[67];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
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
	
	



  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public CMinusScanner_jflex(java.io.Reader in) throws IOException {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 2184) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token scanToken() throws java.io.IOException, ScannerException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
              {
                return new Token(Token.TokenType.EOF_TOKEN);
              }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { throw new RuntimeException("Illegal character \""+yytext()+
                                                              "\" at line "+yyline+", column "+yycolumn);
            }
          case 31: break;
          case 2: 
            { /* ignore */
            }
          case 32: break;
          case 3: 
            { return new Token(Token.TokenType.DIVIDE_TOKEN);
            }
          case 33: break;
          case 4: 
            { return new Token(Token.TokenType.MULTIPLY_TOKEN);
            }
          case 34: break;
          case 5: 
            { return new Token(Token.TokenType.ID_TOKEN, yytext());
            }
          case 35: break;
          case 6: 
            { return new Token(Token.TokenType.INT_TOKEN, new Integer(yytext()));
            }
          case 36: break;
          case 7: 
            { return new Token(Token.TokenType.LEFTPAREN_TOKEN);
            }
          case 37: break;
          case 8: 
            { return new Token(Token.TokenType.RIGHTPAREN_TOKEN);
            }
          case 38: break;
          case 9: 
            { return new Token(Token.TokenType.LEFTCURLYBRACE_TOKEN);
            }
          case 39: break;
          case 10: 
            { return new Token(Token.TokenType.LEFTSQBRACKET_TOKEN);
            }
          case 40: break;
          case 11: 
            { return new Token(Token.TokenType.RIGHTSQBRACKET_TOKEN);
            }
          case 41: break;
          case 12: 
            { return new Token(Token.TokenType.SEMICOLON_TOKEN);
            }
          case 42: break;
          case 13: 
            { return new Token(Token.TokenType.COMMA_TOKEN);
            }
          case 43: break;
          case 14: 
            { return new Token(Token.TokenType.ASSIGN_TOKEN);
            }
          case 44: break;
          case 15: 
            { return new Token(Token.TokenType.GREATERTHAN_TOKEN);
            }
          case 45: break;
          case 16: 
            { return new Token(Token.TokenType.LESSTHAN_TOKEN);
            }
          case 46: break;
          case 17: 
            { return new Token(Token.TokenType.PLUS_TOKEN);
            }
          case 47: break;
          case 18: 
            { return new Token(Token.TokenType.MINUS_TOKEN);
            }
          case 48: break;
          case 19: 
            { throw new ScannerException("Lexical error: Cannot have one IDENTIFIER, "+ "NUMBER, or keyword after another without " + "delimiters in between!");
            }
          case 49: break;
          case 20: 
            { return new Token(Token.TokenType.IF_TOKEN);
            }
          case 50: break;
          case 21: 
            { return new Token(Token.TokenType.EQUALTO_TOKEN);
            }
          case 51: break;
          case 22: 
            { return new Token(Token.TokenType.GREATERTHANEQUALTO_TOKEN);
            }
          case 52: break;
          case 23: 
            { return new Token(Token.TokenType.LESSTHANEQUALTO_TOKEN);
            }
          case 53: break;
          case 24: 
            { return new Token(Token.TokenType.NOTEQUAL_TOKEN);
            }
          case 54: break;
          case 25: 
            { return new Token(Token.TokenType.INT_TOKEN);
            }
          case 55: break;
          case 26: 
            { return new Token(Token.TokenType.ELSE_TOKEN);
            }
          case 56: break;
          case 27: 
            { return new Token(Token.TokenType.VOID_TOKEN);
            }
          case 57: break;
          case 28: 
            { return new Token(Token.TokenType.WHILE_TOKEN);
            }
          case 58: break;
          case 29: 
            { return new Token(Token.TokenType.RETURN_TOKEN);
            }
          case 59: break;
          case 30: 
            { return new Token(Token.TokenType.INT_TOKEN, new Integer(Integer.MIN_VALUE));
            }
          case 60: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
