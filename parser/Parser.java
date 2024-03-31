package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.BeginStatement;
import ast.BoolStatement;
import ast.CharStatement;
import ast.EndStatement;
import ast.Expression;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.IntStatement;
import ast.Program;
import lexer.Lexer;
import token.Token;
import token.TokenType;
import ast.Statement;
import org.junit.Assert;;


public class Parser {
    private Lexer lexer;
    private Token curToken;
    private Token peekToken;
    private List<String> errors;
    private Map<TokenType, PrefixParseFn> prefixParseFns;
    private Map<TokenType, InfixParseFn> infixParseFns;

    public enum OperatorType{
        LOWEST(1),
        EQUALS(2),
        LESSGREATER(3),
        SUM(4),
        PRODUCT(5),
        PREFIX(6);
        
        private final int precedence;
        OperatorType(int precedence){
            this.precedence = precedence;
        }

        public int getPrecedence(){
            return precedence;
        }
    }
    

    public Parser(Lexer lexer){
        this.lexer = lexer;
        prefixParseFns = new HashMap<>();
        infixParseFns = new HashMap<>();
        errors = new ArrayList<>();
        registerPrefix(TokenType.IDENT, this::parseIdentifier);
        nextToken();
        nextToken();

    }


    public void nextToken(){
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }



    public Program ParseProgram() throws Exception{
        
        Program program = new Program(new ArrayList<>());

        while(curToken.getTokenType() != TokenType.EOF){
            Statement stmt = parseStatement();
            if(stmt != null){
                program.addStatement(stmt);
            }
            nextToken();
        }

        return program;
        

    }

    public Expression parseIdentifier(){
        return new Identifier(curToken, curToken.getLiteral());
    }

    public Statement parseStatement() throws Exception {


            switch (curToken.getTokenType()){
                case CHAR:
                return parseCharStatement();
                case INT:
                return parseIntStatement();
                case BOOL:
                return parseBoolStatement();
                
                default:
                    return parseExpressionStatement();
                
            }
        
    }


    public ExpressionStatement parseExpressionStatement() throws Exception{
        ExpressionStatement stmt = new ExpressionStatement();
        stmt.setToken(curToken);

        stmt.setExpression(parseExpression(OperatorType.LOWEST.getPrecedence()));

        if(peekTokenIs(TokenType.EOL)){
            nextToken();
        }
        return stmt;
    }
   
    public Expression parseExpression(int precedence) throws Exception{
        PrefixParseFn prefix = prefixParseFns.get(curToken.getTokenType());
        if(prefix == null){
            return null;
        }
        Expression leftExp = prefix.apply();

        return leftExp;
    }

    public CharStatement parseCharStatement(){
        CharStatement stmt = new CharStatement();
        stmt.setToken(curToken);

        if (!expectPeek(TokenType.IDENT)){
            return null;
        }
        stmt.setName(new Identifier(curToken, curToken.getLiteral()));

        if(!expectPeek(TokenType.ASSIGN)){
            return null;
        }

        while (!curTokenIs(TokenType.EOL)){
            nextToken();
        }
        return stmt;
    }


    public IntStatement parseIntStatement(){
        IntStatement stmt = new IntStatement();
        stmt.setToken(curToken);

        if (!expectPeek(TokenType.IDENT)){
            return null;
        }
        stmt.setName(new Identifier(curToken, curToken.getLiteral()));

        if(!expectPeek(TokenType.ASSIGN)){
            return null;
        }

        while (!curTokenIs(TokenType.EOL)){
            nextToken();
        }
        return stmt;
    }
    
    public BoolStatement parseBoolStatement(){
        BoolStatement stmt = new BoolStatement();
        stmt.setToken(curToken);

        if (!expectPeek(TokenType.IDENT)){
            return null;
        }
        stmt.setName(new Identifier(curToken, curToken.getLiteral()));

        if(!expectPeek(TokenType.ASSIGN)){
            return null;
        }

        while (!curTokenIs(TokenType.EOL)){
            nextToken();
        }
        return stmt;
    }
    

    private boolean curTokenIs(TokenType t){
        return curToken.getTokenType() == t;
    }

    private boolean peekTokenIs(TokenType t){
        return peekToken.getTokenType() == t;
    }

    private boolean expectPeek(TokenType t){
        if(peekTokenIs(t)){
            nextToken();
            return true;
        }else {
            peekError(t);
            return false;
        }
    }

    public List<String> getErrors(){
        return errors;
    }

    public void checkParserErrors(){
        errors = getErrors();
        if(errors.size() == 0){
            return;
        }
        
        StringBuilder message = new StringBuilder(String.format("Parser has %d errors:\n", errors.size()));
        for(String msg : errors){
            message.append("parser error: ").append(msg).append("\n");
        }

        Assert.fail(message.toString());
        

    }

    private void peekError(TokenType t){
        String msg = String.format("expected next token to be %s, got %s instead", t, peekToken.getTokenType());
        errors.add(msg);
    }

    public void registerPrefix(TokenType tokenType, PrefixParseFn fn){
        prefixParseFns.put(tokenType, fn);
    }

    public void registerInfix(TokenType tokenType, InfixParseFn fn){
        infixParseFns.put(tokenType, fn);
    }
    
}
