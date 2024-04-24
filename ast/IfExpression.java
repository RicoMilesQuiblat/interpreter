package ast;

import token.Token;

public class IfExpression implements Expression{
    Token token;
    Expression condition;
    BlockStatement consequence;
    BlockStatement alternative;

    

    public IfExpression() {
    }
    

    public IfExpression(Token token, Expression condition, BlockStatement consequence, BlockStatement alternative) {
        this.token = token;
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }


    @Override
    public void expressionNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getTokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        sb.append("IF");
        sb.append(condition.string());
        sb.append("BEGIN IF\n" + consequence.string());
        sb.append("\nEND IF");

        if(alternative != null){
            sb.append("ELSE");
            sb.append("BEGIN IF\n" + alternative.string());
            sb.append("\nEND IF");
        }
        return sb.toString();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public BlockStatement getConsequence() {
        return consequence;
    }

    public void setConsequence(BlockStatement consequence) {
        this.consequence = consequence;
    }

    public BlockStatement getAlternative() {
        return alternative;
    }

    public void setAlternative(BlockStatement alternative) {
        this.alternative = alternative;
    }
    
}
