package ast;

import token.Token;

public class BoolStatement  implements Statement{
     private Token token;
    private Identifier name;
    private Expression value;

    public BoolStatement(Token token, Identifier name, Expression value){
        this.token = token;
        this.name = name;
        this.value = value;
    }
    public BoolStatement(){
        
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getTokenLiteral() {
        return token.getLiteral();
    }
}