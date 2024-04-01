package evaluator;

import java.beans.Expression;
import java.util.List;

import ast.BoolStatement;
import ast.BooleanExpression;
import ast.CharStatement;
import ast.CharacterExpression;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.InfixExpression;
import ast.IntStatement;
import ast.IntegerLiteral;
import ast.Node;
import ast.PrefixExpression;
import ast.Program;
import ast.Statement;
import object.BeginObject;
import object.BooleanObject;
import object.CharacterObject;
import object.Environment;
import object.Error;
import object.IntegerObject;
import object.NullObject;
import object.Object;
import object.ObjectType;

public class Evaluator {
    private static final BooleanObject TRUE = new BooleanObject(true);
    private static final BooleanObject FALSE = new BooleanObject(false);
    private static final NullObject NULL = new NullObject();
    

    public static Object eval(Node node, Environment env){
        if(node instanceof Program){
            Program program = (Program)node;
            return evalStatements(program.getStatements(), env);

        }else if(node instanceof ExpressionStatement){
            ExpressionStatement exp = (ExpressionStatement)node;
            return eval(exp.getExpression(), env);

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
            Object right = eval(pe.getRight(),env);
            if(isError(right)){
                return right;
            }
            return evalPrefixExpression(pe.getOperator(), right);
        }else if(node instanceof InfixExpression){
            InfixExpression ie = (InfixExpression)node;
            Object left = eval(ie.getLeft(), env);
            if(isError(left)){
                return left;
            }

            Object right = eval(ie.getRight() ,env);
            if(isError(right)){
                return right;
            }

            return evalInfixExpression(ie.getOperator(), left, right);
        }else if(node instanceof IntStatement){
            IntStatement is = (IntStatement)node;
            Object val = eval(is.getValue(), env);
            if(isError(val)){
                return val;
            }
            env.set(is.getName().getValue(), val);
            return val;
            
        }else if(node instanceof CharStatement){
            CharStatement cs = (CharStatement)node;
            Object val = eval(cs.getValue(), env);
            if(isError(val)){
                return val;
            }
            env.set(cs.getName().getValue(), val);
            return val;

        }else if(node instanceof BoolStatement){
            BoolStatement bs = (BoolStatement)node;
            Object val = eval(bs.getValue(), env);
            if(isError(val)){
                return val;
            }
            env.set(bs.getName().getValue(), val);
            return val;

        }else if(node instanceof Identifier){
            Identifier id = (Identifier) node;
            return evalIdentifier(id, env);
        }
        return NULL;
    }

    
    

    private static Object evalStatements(List<Statement> stmts, Environment env){
        Object result = null;
        for(Statement stmt: stmts){
            result = eval(stmt, env);

            if(result.type().equals(ObjectType.ERROR_OBJ)){
                return result;
            }
        }
        return result;
    }

    private static Object evalIdentifier(Identifier node, Environment env){
        Object value;
        if(env.has(node.getValue())){
            value = env.get(node.getValue());
        }else{
            return newError("identifier not found: " + node.getValue());
        }
        return value;

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

    private static boolean isError(Object obj){
        if(obj != null){
            return obj.type().equals(ObjectType.ERROR_OBJ);
        }
        return false;   
    }
}
