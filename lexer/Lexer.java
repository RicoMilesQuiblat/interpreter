package lexer;

import token.TokenType;

import java.util.Arrays;
import java.util.List;

import token.Token;

public class Lexer {
    private String input;
    private int position;
    private int readPosition;
    private char ch;

    public Lexer(String input){
        this.input = input;
        this.position = 0;
        this.readPosition = 0;
        this.ch = '\0';
        readChar();
    }

    

    public void readChar(){
        if(readPosition >= input.length()){
            ch = 0;
        }else {
            ch=input.charAt(readPosition);
        }
        position = readPosition;
        readPosition++;
    }

    public Token nextToken(){
        Token tok = new Token();

        skipWhiteSpace();

        switch (ch) {
            case '=':
                tok = newToken(TokenType.ASSIGN, ch);
                break;
            case '+':
                tok = newToken(TokenType.PLUS, ch);
                break;
            case 0:
                tok.setLiteral("");
                tok.setTokenType(TokenType.EOF);
                break;
            default:
                if(isLetter(ch)){
                    tok.setLiteral(readIdentifier());
                    tok.setTokenType(tok.lookupIdent(tok.getLiteral()));
                    return tok;
                }else if(isDigit(ch)){
                    tok.setTokenType(TokenType.DIGIT);
                    tok.setLiteral(readNumber());
                    return tok;
                }else {
                    tok = newToken(TokenType.ILLEGAL, ch);

                }
        }
        readChar();

        return tok;
    }

    private String readIdentifier(){
        int tempPosition = position;
        while(isLetter(ch)){
            readChar();
        }
        if(ch == ' ' ){
            if(input.substring(tempPosition, position).equals("BEGIN") || input.substring(tempPosition, position).equals("END")){
                
                readChar();
                while(isLetter(ch)){
                    readChar();
                }
            }
        }
        return input.substring(tempPosition, position);
    }

    private String readNumber(){
        int tempPosition = position;
        while(isDigit(ch)){
            readChar();
        }
        

        return input.substring(tempPosition, position);
    }

    private boolean isLetter(char ch){
        return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_';
    }

    private boolean isDigit(char ch){
        return '0' <= ch && ch <= '9';
    }

    private void skipWhiteSpace(){
        while(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'){
            readChar();
        }
    }

    public static Token newToken(TokenType tokenType, char ch){
        return new Token(tokenType, String.valueOf(ch));
    }



}
