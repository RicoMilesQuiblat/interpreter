package evaluator;

import java.beans.Expression;
import java.util.List;

import ast.BooleanExpression;
import ast.CharacterExpression;
import ast.ExpressionStatement;
import ast.IntegerLiteral;
import ast.Node;
import ast.Program;
import ast.Statement;
import object.BooleanObject;
import object.CharacterObject;
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
    
}
