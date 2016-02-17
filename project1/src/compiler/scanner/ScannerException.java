package compiler.scanner;

/**
 * This exception class occurs when a lexical error has happened.
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 */
public class ScannerException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public ScannerException() { super(); }
	public ScannerException(String message) { super(message); }
	public ScannerException(String message, Throwable cause) {
		super(message, cause); }
	public ScannerException(Throwable cause) { super(cause); }
}
