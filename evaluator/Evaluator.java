package evaluator;

import java.beans.Expression;
import java.util.List;

import ast.BooleanExpression;
import ast.CharacterExpression;
import ast.ExpressionStatement;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.Node;
import ast.PrefixExpression;
import ast.Program;
import ast.Statement;
import object.BooleanObject;
import object.CharacterObject;
import object.Error;
import object.IntegerObject;
import object.NullObject;
import object.Object;
import object.ObjectType;

public class Evaluator {
    private static final BooleanObject TRUE = new BooleanObject(true);
    private static final BooleanObject FALSE = new BooleanObject(false);
    private static final NullObject NULL = new NullObject();

    public static Object eval(Node node){
        if(node instanceof Program){
            Program program = (Program)node;
            return evalStatements(program.getStatements());

        }else if(node instanceof ExpressionStatement){
            ExpressionStatement exp = (ExpressionStatement)node;
            return eval(exp.getExpression());

        }else if(node instanceof IntegerLiteral){
            
            IntegerLiteral inlit = (IntegerLiteral) node;
            return new IntegerObject(inlit.getValue());

        }else if(node instanceof CharacterExpression){
            CharacterExpression ce = (CharacterExpression)node;
            return new CharacterObject(ce.getValue());
        }else if(node instanceof BooleanExpression){
            BooleanExpression be = (BooleanExpression)node;
            return nativeBoolToBooleanObject(be.getValue());
        }else if(node instanceof PrefixExpression){
            PrefixExpression pe = (PrefixExpression)node;
            Object right = eval(pe.getRight());
            return evalPrefixExpression(pe.getOperator(), right);
        }else if(node instanceof InfixExpression){
            InfixExpression ie = (InfixExpression)node;
            Object left = eval(ie.getLeft());
            Object right = eval(ie.getRight());
            return evalInfixExpression(ie.getOperator(), left, right);
        }
        return NULL;
    }

    private static Object evalStatements(List<Statement> stmts){
        Object result = null;
        for(Statement stmt: stmts){
            result = eval(stmt);
        }
        return result;
    }

    private static BooleanObject nativeBoolToBooleanObject(boolean input){
        if(input){
            return TRUE;
        }
        return FALSE;

    }

    private static Object evalInfixExpression(String operator, Object left, Object right){
        if(left.type().equals(ObjectType.INTEGER_OBJ) && right.type().equals(ObjectType.INTEGER_OBJ)){
            return evalIntegerInfixExpression(operator, left, right);
        }else if(!(left.type().equals(right.type()))){
            return newError("type mismatch: %s %s %s", left.type(), operator, right.type());
        }
        return newError("unkown operator: %s %s %s", left.type(), operator, right.type());
    }

    private static Object evalIntegerInfixExpression(String operator, Object left, Object right){
        IntegerObject leftObj = (IntegerObject)left;
        IntegerObject rightObj = (IntegerObject)right;

        int leftVal = leftObj.getValue();
        int rightVal = rightObj.getValue();

        switch (operator){
            case "+":
                return new IntegerObject(leftVal + rightVal);
            case "-":
                return new IntegerObject(leftVal - rightVal);
            case "*":
                return new IntegerObject(leftVal * rightVal);
            case "/":
                return new IntegerObject(leftVal / rightVal);
            case "%":
                return new IntegerObject(leftVal % rightVal);
            case ">":
                return nativeBoolToBooleanObject(leftVal > rightVal);
            case "<":
                return nativeBoolToBooleanObject(leftVal < rightVal);
            case ">=":
                return nativeBoolToBooleanObject(leftVal >= rightVal);
            case "<=":
                return nativeBoolToBooleanObject(leftVal <= rightVal);
            case "==":
                return nativeBoolToBooleanObject(leftVal == rightVal);
            case "<>":
                return nativeBoolToBooleanObject(leftVal != rightVal);
            default:
            return NULL;
        }
    }
    
    private static Object evalPrefixExpression(String operator, Object right){
        switch (operator){
            case "-":
                
                return evalMinusPrefixOperatorExpression(right);
            default:
                return newError("unknown operator: %s%s", operator, right.type());
        }
    }

    private static Object evalMinusPrefixOperatorExpression(Object right){
        if(!(right.type().equals(ObjectType.INTEGER_OBJ)) ){
            return NULL;
        }
        
        IntegerObject obj = (IntegerObject)right;
        int value = obj.getValue();
        return new IntegerObject(value * -1);
    }

    private static Error newError(String format, java.lang.Object... a){
        return new Error(String.format(format, a));
    }
}
