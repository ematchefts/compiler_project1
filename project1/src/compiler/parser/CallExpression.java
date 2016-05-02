package compiler.parser;

import java.util.ArrayList;
import lowlevel.*;

/**
 * This class is a subclass of Expression.
 * It stores expressions of the form call(params)
 * 
 * @author Elizabeth Matchefts & Benjamin Seymour
 *
 */
public class CallExpression extends Expression {

    private String call;
    private ArrayList<Expression> args;

    /**
     * Call Expression constructor
     * 
     * @param c The call of the call expression
     * @param a An array list of the arguments in the call parameters
     */
    public CallExpression(String c, ArrayList<Expression> a) {
        call = c;
        args = a;
    }

    /**
     * This function prints a call expression.
     */
    @Override
    public void print(String x) {
        System.out.println(x + "call: " + call);
        System.out.println(x + "    " + "params:");
        for (Expression arg :args) {
            arg.print(x + "    ");
        }
    }
    
    @Override
    public void genCode(Function f) {
        //Generate pass operations
        for (int i = args.size() - 1; i >= 0; i--) {
            args.get(i).genCode(f);
            Operation op = new Operation(Operation.OperationType.PASS, f.getCurrBlock());

            Operand pass;
            if (args.get(i) instanceof NumExpression) {
                pass = new Operand(Operand.OperandType.INTEGER, ((NumExpression) args.get(i)).getNum());
            } else {
                pass = new Operand(Operand.OperandType.REGISTER, args.get(i).getRegNum());
            }
            op.setSrcOperand(0, pass);
            op.addAttribute(new Attribute("PARAM_NUM", Integer.toString(i)));
            f.getCurrBlock().appendOper(op);
        }
        //Generate call operation
        Operation op = new Operation(Operation.OperationType.CALL, f.getCurrBlock());
        Operand funcName = new Operand(Operand.OperandType.STRING, call);
        op.setSrcOperand(0, funcName);

        op.addAttribute(new Attribute("numParams", Integer.toString(args.size())));
        f.getCurrBlock().appendOper(op);

        // Move return register into regular register.
        // Transfer retreg into callexpr.regNum
        // ASSIGN srcReg retReg;
        setRegNum(f.getNewRegNum());
        Operand save = new Operand(Operand.OperandType.REGISTER, regNum);
        Operand ret = new Operand(Operand.OperandType.MACRO, "RetReg");
        Operation saveReturn = new Operation(Operation.OperationType.ASSIGN, f.getCurrBlock());
        saveReturn.setSrcOperand(0, ret);
        saveReturn.setDestOperand(0, save);
        f.getCurrBlock().appendOper(saveReturn);
    }
}
