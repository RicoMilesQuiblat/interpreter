package parser;

import java.beans.Expression;

public interface InfixParseFn {
    Expression apply(Expression left);

}
