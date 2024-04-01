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
                if(peekChar() == '='){
                    char tempCh = ch;
                    readChar();
                    String finalLiteral = Character.toString(tempCh) + Character.toString(ch);
                    tok.setLiteral(finalLiteral);
                    tok.setTokenType(TokenType.EQUAL);
                }else{
                    tok = newToken(TokenType.ASSIGN, ch);
                }
                break;
            case '(':
                tok = newToken(TokenType.LPARA, ch);
                break;
            case ')':
                tok = newToken(TokenType.RPARA, ch);
                break;
            case '+':
                tok = newToken(TokenType.PLUS, ch);
                break;
            case '-':
                tok = newToken(TokenType.SUBTRACT, ch);
                break;
            case '*':
                tok = newToken(TokenType.MULTIPLY, ch);
                break;
            case '/':
                tok = newToken(TokenType.DIVIDE, ch);
                break;
            case '%':
                tok = newToken(TokenType.MODULO, ch);
                break;
            case '>':
                if(peekChar() == '='){
                    char tempCh = ch;
                    readChar();
                    String finalLiteral = Character.toString(tempCh) + Character.toString(ch);
                    tok.setLiteral(finalLiteral);
                    tok.setTokenType(TokenType.GREATEQ);
                }else{
                    tok = newToken(TokenType.GREAT, ch);
                }
                break;
            case '<':
                if(peekChar() == '='){
                    char tempCh = ch;
                    readChar();
                    String finalLiteral = Character.toString(tempCh) + Character.toString(ch);
                    tok.setLiteral(finalLiteral);
                    tok.setTokenType(TokenType.LESSEQ);
                }else if(peekChar() == '>'){
                    char tempCh = ch;
                    
                    readChar();
                    String finalLiteral = Character.toString(tempCh) + Character.toString(ch);
                    tok.setLiteral(finalLiteral);
                    tok.setTokenType(TokenType.NOTEQUAL);
                }else {
                    tok = newToken(TokenType.LESS, ch);
                }
                break;
            case '[':
                tok = newToken(TokenType.LESCAPE, ch);
                break;
            case ']':
                tok = newToken(TokenType.RESCAPE, ch);
                break;
            case '$':
                tok = newToken(TokenType.EOL, ch);
                break;
            case '\'':
                readChar();
                char tempCh = ch;
                readChar();

                if(ch == '\''){
                    tok = newToken(TokenType.CHARACTER, tempCh);
                }else{
                    tok = newToken(TokenType.ILLEGAL, ch);
                }
                break;
            case '#':
                tok .setLiteral(readComment());
                tok.setTokenType(TokenType.COMMENT);
                break;
            
            case '\"':
                tok.setLiteral(readBoolean());
                if(tok.getLiteral().equals("TRUE")){
                    tok.setTokenType(TokenType.TRUE);
                }else if(tok.getLiteral().equals("FALSE")){
                    tok.setTokenType(TokenType.FALSE);
                }else{
                    tok.setLiteral("");
                    tok.setTokenType(TokenType.ILLEGAL);
                }
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

    private char peekChar(){
        if (readPosition >= input.length()){
            return 0;
        }else{
            return input.charAt(readPosition);
        }
    }

    private String readIdentifier(){
        int tempPosition = position;

    
        while(isLetter(ch)){
            readChar();
        }
        if(ch == ' ' ){
            if(input.substring(tempPosition, position).equals("END")){
                readChar();
                while(isLetter(ch)){
                    readChar();
                }
            }
        }
        return input.substring(tempPosition, position);
    }

    private String readComment(){
        int tempPosition = position;
        while(peekChar() != '\n' || peekChar() != 0){
            readChar();
        }
        

        return input.substring(tempPosition, position);
    }

    private String readBoolean(){
        readChar();
        int tempPosition = position;
        while(ch != '"'){
            readChar();
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
