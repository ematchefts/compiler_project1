package compiler.parser;
import lowlevel.*;

/**
 * This class is a subclass of Expression.
 * It contains number expressions
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class NumExpression extends Expression {

    private int num;

    /**
     * Number Expression constructor
     * @param n the number
     */
    public NumExpression(int n) {
        num = n;
    }

    /**
     * num getter
     * @return num The number inside the number expression
     */
    public int getNum() {
        return num;
    }

    /**
     * Print function for number expression
     */
    @Override
    public void print(String x) {
        System.out.print(x + num + "\n");
    }
    
    public void genCode(Function f) {
        if (!f.getTable().containsKey(num)) {
            regNum = f.getNewRegNum();
            f.getTable().put(num, regNum);
        }else{
            regNum = (int) f.getTable().get(num);
        }
    }
}
