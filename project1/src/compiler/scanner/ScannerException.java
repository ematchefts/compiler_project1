package compiler.scanner;

public class ScannerException extends Exception{
	// Eclipse thinks we need this field for some reason?
	private static final long serialVersionUID = 1L;
	
	public ScannerException() { super(); }
	public ScannerException(String message) { super(message); }
	public ScannerException(String message, Throwable cause) {
		super(message, cause); }
	public ScannerException(Throwable cause) { super(cause); }
}
