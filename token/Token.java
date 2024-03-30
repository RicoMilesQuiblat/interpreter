package token;

import java.util.HashMap;
import java.util.Map;

public class Token {
    private TokenType tokenType;
    private String literal;
    private final Map<String, TokenType> keywords;

    public Token(TokenType tokenType, String literal){
        this.tokenType = tokenType;
        this.literal = literal;
        this.keywords = new HashMap<>();
        initKeywords();
    }
    public Token(){
        this.keywords = new HashMap<>();
        initKeywords();
    }

    private void initKeywords(){
        keywords.put("CHAR", TokenType.CHAR);
        keywords.put("INT", TokenType.INT);
        keywords.put("BOOL", TokenType.BOOL);
        keywords.put("BEGIN CODE", TokenType.START);
        keywords.put("END CODE", TokenType.END);
        keywords.put("DISPLAY:", TokenType.DISPLAY);
        keywords.put("SCAN:", TokenType.SCAN);
    }

    public TokenType lookupIdent(String ident){
        if(keywords.containsKey(ident)){
            return keywords.get(ident);
        }
        return TokenType.IDENT;
    }

    public void setTokenType(TokenType tokenType){
        this.tokenType = tokenType;
    }
    public TokenType getTokenType(){
        return tokenType;
    }

    public void setLiteral(String literal){
        this.literal = literal;
    }
    public String getLiteral(){
        return literal;
    }

    public String toString(){
        return "Token{" + "type = \"" + tokenType + "\" literal = \"" +  literal + "\"}";
    }
}