package ast;

import token.Token;

public class EndStatement implements Statement{
        private Token token;
    
    public EndStatement(){
        
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

    @Override
    public String string() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'string'");
    }

}
