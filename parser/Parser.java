package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.BeginStatement;
import ast.BoolStatement;
import ast.BooleanExpression;
import ast.CharStatement;
import ast.CharacterExpression;
import ast.EndStatement;
import ast.Expression;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.InfixExpression;
import ast.IntStatement;
import ast.IntegerLiteral;
import ast.PrefixExpression;
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
    private Map<TokenType, Integer> infixPrecedences;

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
        infixPrecedences = new HashMap<>();
        initPrecedences();
        errors = new ArrayList<>();
        registerExpressions();
        nextToken();
        nextToken();

    }

    private void initPrecedences(){
        infixPrecedences.put(TokenType.EQUAL, OperatorType.EQUALS.getPrecedence());
        infixPrecedences.put(TokenType.NOTEQUAL, OperatorType.EQUALS.getPrecedence());
        infixPrecedences.put(TokenType.LESS, OperatorType.LESSGREATER.getPrecedence());
        infixPrecedences.put(TokenType.GREAT, OperatorType.LESSGREATER.getPrecedence());
        infixPrecedences.put(TokenType. LESSEQ, OperatorType.LESSGREATER.getPrecedence());
        infixPrecedences.put(TokenType.GREATEQ, OperatorType.LESSGREATER.getPrecedence());
        infixPrecedences.put(TokenType.PLUS, OperatorType.SUM.getPrecedence());
        infixPrecedences.put(TokenType.SUBTRACT, OperatorType.SUM.getPrecedence());
        infixPrecedences.put(TokenType.MULTIPLY, OperatorType.PRODUCT.getPrecedence());
        infixPrecedences.put(TokenType.DIVIDE, OperatorType.PRODUCT.getPrecedence());
        infixPrecedences.put(TokenType.MODULO, OperatorType.PRODUCT.getPrecedence());
    }

    private void registerExpressions(){
        registerPrefix(TokenType.IDENT, this::parseIdentifier);
        registerPrefix(TokenType.DIGIT, this::parseIntegerLiteral);
        registerPrefix(TokenType.SUBTRACT, this::parsePrefixExpression);
        registerInfix(TokenType.PLUS, this::parseInfixExpression);
        registerInfix(TokenType.SUBTRACT, this::parseInfixExpression);
        registerInfix(TokenType.MULTIPLY, this::parseInfixExpression);
        registerInfix(TokenType.DIVIDE, this::parseInfixExpression);
        registerInfix(TokenType.MODULO, this::parseInfixExpression);
        registerInfix(TokenType.EQUAL, this::parseInfixExpression);
        registerInfix(TokenType.NOTEQUAL, this::parseInfixExpression);
        registerInfix(TokenType.GREAT, this::parseInfixExpression);
        registerInfix(TokenType.LESS, this::parseInfixExpression);
        registerInfix(TokenType.GREATEQ, this::parseInfixExpression);
        registerInfix(TokenType.LESSEQ, this::parseInfixExpression);
        registerPrefix(TokenType.TRUE, this::parseBoolean);
        registerPrefix(TokenType.FALSE, this::parseBoolean);
        registerPrefix(TokenType.CHARACTER, this::parseCharacter);
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

    public Expression parseBoolean(){
        return new BooleanExpression(curToken, curTokenIs(TokenType.TRUE));
    }

    public Expression parseCharacter(){
        return new CharacterExpression(curToken, curToken.getLiteral().charAt(0));
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

    public Expression parseIntegerLiteral(){
        
        IntegerLiteral literal = new IntegerLiteral();
        literal.setToken(curToken);
        int value;
        try{
            value = Integer.parseInt(curToken.getLiteral(), 10);
        }catch(NumberFormatException e){
            String msg = String.format("Could not parse %s as integer", curToken.getLiteral());
            System.err.println(msg);
            errors.add(msg);
            return null;
        }

        literal.setValue(value);
        
        return literal;

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
            noPrefixParseFNError(curToken.getTokenType());
            
            return null;
        }
        Expression leftExp = prefix.apply();

        while(!peekTokenIs(TokenType.EOL) && precedence < peekPrecedence()){
            InfixParseFn infix = infixParseFns.get(peekToken.getTokenType());
            if(infix == null){
                
                return leftExp;
            }
            nextToken();
            leftExp = infix.apply(leftExp);
        }
        
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

        nextToken();

        try {
            stmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (peekTokenIs(TokenType.EOL)){
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

        nextToken();

        try {
            stmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (peekTokenIs(TokenType.EOL)){
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

        nextToken();

        try {
            stmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (peekTokenIs(TokenType.EOL)){
            nextToken();
        }
        return stmt;
    }
    public Expression parsePrefixExpression(){
       
        PrefixExpression expression = new PrefixExpression();
        expression.setToken(curToken);
        expression.setOperator(curToken.getLiteral());

        nextToken();
        
        try {
            expression.setRight(parseExpression(OperatorType.PREFIX.getPrecedence()));
        } catch (Exception e) {
            e.printStackTrace();
        }
       

        return expression;
    }

    public Expression parseInfixExpression(Expression left){
        InfixExpression expression = new InfixExpression();
        expression.setToken(curToken);
        expression.setOperator(curToken.getLiteral());
        expression.setLeft(left);

        int precedence = curPrecedence();
        nextToken();
        try {
            expression.setRight(parseExpression(precedence));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expression;
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

    private int curPrecedence(){
        Integer p = infixPrecedences.get(curToken.getTokenType());
        if(p != null){
            return p.intValue();
        }

        return OperatorType.LOWEST.getPrecedence();
    }

    private int peekPrecedence(){
        Integer p = infixPrecedences.get(peekToken.getTokenType());
        if(p != null){
            return p.intValue();
        }
        return OperatorType.LOWEST.getPrecedence();

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

    public void noPrefixParseFNError(TokenType t){
        String msg = String.format("no prefix parse function for %s found", t.getLiteral());
        errors.add(msg);
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
