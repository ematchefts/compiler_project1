package compiler.parser;

public class ParserException extends Exception {

	/**
     * Creates a new instance of <code>ParseException</code> without detail
     * message.
     */
    public ParserException() {
    }

    /**
     * Constructs an instance of <code>ParseException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ParserException(String msg) {
        super(msg);
    }
}
