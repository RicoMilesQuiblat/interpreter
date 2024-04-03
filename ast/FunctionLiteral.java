package ast;

import java.util.ArrayList;
import java.util.List;

import token.Token;

public class FunctionLiteral implements Expression{
    Token token;
    List<Identifier> parameters;
    BlockStatement body;

    public FunctionLiteral(Token token, List<Identifier> parameters, BlockStatement body) {
        this.token = token;
        this.parameters = parameters;
        this.body = body;
    }

    public FunctionLiteral() {
        parameters = new ArrayList<>();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public void setParameters(List<Identifier> parameters) {
        this.parameters = parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
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
        StringBuilder out = new StringBuilder();

        List<String> params = new ArrayList<>();
        for(Identifier id : parameters){
            params.add(id.string());
        }
        out.append(getTokenLiteral());
        out.append("(");
        out.append(String.join(",", params));
        out.append(")");
        out.append(body.string());

        return out.toString();
    }
    
    

    
    
}
