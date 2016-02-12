package compiler.scanner;

/**
 * This class defines the states possible for a CMinusScanner
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class State {
	
	public enum StateType {
		START,
		COMMENT,
		ID,
		NUM,
		DONE
	}

}
