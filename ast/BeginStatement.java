package ast;

import token.Token;

public class BeginStatement implements Statement{
    
    private Token token;
    
    public BeginStatement(){
        
    }
    
    public Token getToken() {
        return token;
    }
    
    public void setToken(Token token) {
        this.token = token;
    }
    
    @Override
    public void statementNode() {
        
        
    }

    @Override
    public String getTokenLiteral() {
        return token.getLiteral();
    }

    
    
}
