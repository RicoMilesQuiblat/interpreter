package parser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import ast.BeginExpression;
import ast.BlockStatement;
import ast.BoolStatement;
import ast.BooleanExpression;
import ast.CharStatement;
import ast.CharacterExpression;
import ast.DisplayExpression;
import ast.Expression;
import ast.ExpressionStatement;
import ast.FloatLiteral;
import ast.FloatStatement;
import ast.FunctionLiteral;
import ast.Identifier;
import ast.InfixExpression;
import ast.IntStatement;
import ast.IntegerLiteral;
import ast.PrefixExpression;
import ast.Program;
import ast.ScanExpression;
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
    private Map<String, Statement> statementsList; 
    private Boolean hasStarted;
    private Boolean functionStarted;
    private Boolean variableDeclarationStarted;
    private Boolean executableStarted;
    private int statementsCount;
    private Program program;
    private List<Statement> tempStatementList;


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
    private List<String> reservedWords;
    

    public Parser(Lexer lexer){
        tempStatementList = new ArrayList<>();
        program = new Program(new ArrayList<>());
        this.lexer = lexer;
        prefixParseFns = new HashMap<>();
        infixParseFns = new HashMap<>();
        infixPrecedences = new HashMap<>();
        reservedWords = new ArrayList<>();
        statementsList = new HashMap<>();
        hasStarted = false;
        functionStarted = false;
        executableStarted = false;
        variableDeclarationStarted =false;
        statementsCount = 1;
        initPrecedences();
        initReservedWords();
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

    private void initReservedWords(){
        reservedWords.add("CHAR");
        reservedWords.add("INT");
        reservedWords.add("BOOL");
        reservedWords.add("FLOAT");
        reservedWords.add("DISPLAY");
        reservedWords.add("SCAN");
        reservedWords.add("BEGIN");
        reservedWords.add("END");
    }

    private void registerExpressions(){
        registerPrefix(TokenType.START, this::parseBeginExpression) ;
        registerPrefix(TokenType.IDENT, this::parseIdentifier);
        registerPrefix(TokenType.LPARA, this::parseGroupedExpression);
        registerPrefix(TokenType.INTEGER, this::parseIntegerLiteral);
        registerPrefix(TokenType.FLOATINGPOINT, this::parseFloatLiteral);
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
        registerPrefix(TokenType.FUNCTION, this::parseFunctionLiteral);
        registerPrefix(TokenType.DISPLAY, this::parseDisplayExpression);
        registerPrefix(TokenType.SCAN, this::parseScanExpression);
    }


    public void nextToken(){
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    


    public Program ParseProgram() throws Exception{
        
    
        while(curToken.getTokenType() != TokenType.EOF){
            List<Statement> stmt = parseStatement();
            if(stmt != null){
                for(Statement statement: stmt){
                    program.addStatement(statement);
                }
            }
            nextToken();
        }
        

        return program;
        

    }

    public Expression parseIdentifier(){
        String ident = curToken.getLiteral();
        return new Identifier(curToken, ident);
    }

    public Expression parseBoolean(){
        return new BooleanExpression(curToken, curTokenIs(TokenType.TRUE));
    }

    public Expression parseCharacter(){
        return new CharacterExpression(curToken, curToken.getLiteral().charAt(0));
    }

    public List<Statement> parseStatement() throws Exception {
           tempStatementList = new ArrayList<>();

            switch (curToken.getTokenType()){
                case CHAR:
                    if(executableStarted){
                        errors.add("Cannot Declare Variable after Executable code");
                        return null;
                    }
                    variableDeclarationStarted = true;
                    parseCharStatement();
                    return tempStatementList;
                case INT:
                if(executableStarted){
                    errors.add("Cannot Declare Variable after Executable code");
                    return null;
                }
                variableDeclarationStarted = true;
                parseIntStatement();

                return tempStatementList;
                case BOOL:
                if(executableStarted){
                    errors.add("Cannot Declare Variable after Executable code");
                    return null;
                }
                parseBoolStatement();
                variableDeclarationStarted = true;
                return tempStatementList;
                case FLOAT:
                if(executableStarted){
                    errors.add("Cannot Declare Variable after Executable code");
                    return null;
                }
                variableDeclarationStarted = true;
                parseFloatStatement();
                return tempStatementList;
                case ILLEGAL:
                    errors.add(String.format("Illegal token %s", curToken.getLiteral()));
                    return null;
                default:
                    if(curTokenIs(TokenType.IDENT) && peekTokenIs(TokenType.ASSIGN)){
                        parseReassignment();
                        return tempStatementList;
                    }
                    List<Statement> tempExp = new ArrayList<>();
                    tempExp.add(parseExpressionStatement());
                    return tempExp;
                
            }
        
    }

    private void parseReassignment(){
        String ident = curToken.getLiteral();
        if(statementsList.containsKey(curToken.getLiteral())){
            if(statementsList.get(ident) instanceof IntStatement){
                IntStatement newStmt = new IntStatement();
                newStmt.setToken(new Token(TokenType.INT, "INT"));
                newStmt.setName(new Identifier(curToken, ident));
                try {
                    nextToken();
                    nextToken();
                            
                    try {
                        newStmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    }
                    tempStatementList.add(newStmt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
            }else if(statementsList.get(ident) instanceof FloatStatement){
                if(statementsList.get(ident) instanceof IntStatement){
                    FloatStatement newStmt = new FloatStatement();
                    newStmt.setToken(new Token(TokenType.INT, "INT"));
                    newStmt.setName(new Identifier(curToken, ident));
                    try {
                        nextToken();
                        nextToken();
                                
                        try {
                            newStmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            
                        }
                        tempStatementList.add(newStmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
            }else if(statementsList.get(ident) instanceof BoolStatement){
                if(statementsList.get(ident) instanceof IntStatement){
                    BoolStatement newStmt = new BoolStatement();
                    newStmt.setToken(new Token(TokenType.INT, "INT"));
                    newStmt.setName(new Identifier(curToken, ident));
                    try {
                        nextToken();
                        nextToken();
                                
                        try {
                            newStmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            
                        }
                        tempStatementList.add(newStmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
            }else if(statementsList.get(ident) instanceof CharStatement){
                if(statementsList.get(ident) instanceof IntStatement){
                    CharStatement newStmt = new CharStatement();
                    newStmt.setToken(new Token(TokenType.INT, "INT"));
                    newStmt.setName(new Identifier(curToken, ident));
                    try {
                        nextToken();
                        nextToken();
                                
                        try {
                            newStmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            
                        }
                        tempStatementList.add(newStmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            
            }
        }
            


    private BeginExpression parseBeginExpression(){
        executableStarted = false;
        variableDeclarationStarted = false;
        statementsList = new HashMap<>();
        BeginExpression exp = new BeginExpression();
        exp.setToken(curToken);
        nextToken();

        try {
            Identifier ident = (Identifier)parseExpression(OperatorType.LOWEST.getPrecedence());
            if(!ident.getValue().equals("CODE") && !ident.getValue().equals("FUNCTION")){
                errors.add("Invalid Begin Statement");
                return null;
            }
            if(ident.getValue().equals("CODE")){

                hasStarted = true;
                exp.setBody(parseBlockStatement(ident.getValue()));
            }else if(ident.getValue().equals("FUNCTION")){
                functionStarted = true;
            }
            exp.setIdent(ident); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    

        statementsCount++;

        return exp;
    }

    private DisplayExpression parseDisplayExpression(){
        if(Lexer.getLine() - 1 < statementsCount){
            errors.add("More than one statement per line is not allowed");
            return null;
        }
        DisplayExpression exp = new DisplayExpression();
        if(!variableDeclarationStarted){
            errors.add("Executable code before variable declaration is invalid");
            return null;
        }
        executableStarted = true;
        exp.setToken(curToken);
        if(!expectPeek(TokenType.COLON)){
            return null;
        }
        nextToken();
        List<Object> all = new ArrayList<>();
        try {
            all.add(parseExpression(OperatorType.LOWEST.getPrecedence()));
        } catch (Exception e) {
            
        }
        while(peekTokenIs(TokenType.CONCAT)){
            nextToken();
            all.add(curToken);
            nextToken();
            if(curTokenIs(TokenType.ESCAPE) || curTokenIs(TokenType.EOL)){
                all.add(curToken);
                continue;
            }else if(curTokenIs(TokenType.ESCAPE)){
                errors.add("Invalid Concatenation ");
                return null;
            }

            try {
                all.add(parseExpression(OperatorType.LOWEST.getPrecedence()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        exp.setBody(all);
        statementsCount++;
        return exp;
        
    }

    private ScanExpression parseScanExpression(){
        if(Lexer.getLine() - 1 < statementsCount){
            errors.add("More than one statement per line is not allowed");
            return null;
        }
        if(!variableDeclarationStarted){
            errors.add("Executable code before variable declaration is invalid");
            return null;
        }
        executableStarted = true;
        ScanExpression exp = new ScanExpression();
        List<String> idents = new ArrayList<>();
        exp.setToken(curToken);
        if(!expectPeek(TokenType.COLON)){
            return null;
        }
        if(!expectPeek(TokenType.IDENT)){
            return null;
        }

        if(statementsList.containsKey(curToken.getLiteral())){
            idents.add(curToken.getLiteral());
        }else {
            errors.add(String.format("Identifier %s does not exist", curToken.getLiteral()));
            return null;
        }

        while(peekTokenIs(TokenType.COMMA)){
            nextToken();
            if(!expectPeek(TokenType.IDENT)){
                return null;
            }
    
            if(statementsList.containsKey(curToken.getLiteral())){
                idents.add(curToken.getLiteral());
            }else {
                errors.add(String.format("Identifier %s does not exist", curToken.getLiteral()));
                return null;
            }
        }
        List<Expression> expressions = startScanning();
        if(idents.size() != expressions.size()){
            errors.add("Not enough arguments for scan");
            return null;
        }
        assignScan(idents, expressions);
        return exp;

    }
    private void assignScan(List<String> idents, List<Expression> expressions){
        for(int i = 0; i < idents.size(); i++){
            if(statementsList.containsKey(idents.get(i))){
                if(statementsList.get(idents.get(i)) instanceof IntStatement){
                    IntStatement is = (IntStatement) statementsList.get(idents.get(i));
                    is.setValue(expressions.get(i));
                }else if(statementsList.get(idents.get(i)) instanceof CharStatement){
                    CharStatement is = (CharStatement) statementsList.get(idents.get(i));
                    is.setValue(expressions.get(i));
                }else if(statementsList.get(idents.get(i)) instanceof BoolStatement){
                    BoolStatement is = (BoolStatement) statementsList.get(idents.get(i));
                    is.setValue(expressions.get(i));
                }else if(statementsList.get(idents.get(i)) instanceof FloatStatement){
                    FloatStatement is = (FloatStatement) statementsList.get(idents.get(i));
                    is.setValue(expressions.get(i));
                }
            }
        }
    }

    private List<Expression> startScanning(){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        List<Expression> expressions = new ArrayList<>();
        String line = "";
        
        System.out.println("Enter Input: ");
        System.out.print(">> ");
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        
    
        Lexer l = new Lexer(line);
        Parser p = new Parser(l);

        try {
            expressions.add(p.parseExpression(OperatorType.LOWEST.getPrecedence()));
            while(p.peekTokenIs(TokenType.COMMA)){
                p.nextToken();
                p.nextToken();
                expressions.add(p.parseExpression(OperatorType.LOWEST.getPrecedence()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expressions;
    }

    
    private Expression parseFunctionLiteral(){
        FunctionLiteral lit = new FunctionLiteral();
        lit.setToken(curToken);

        if(!expectPeek(TokenType.LPARA)){
            return null;
        }

        lit.setParameters(parseFunctionParameters());

        if(!expectPeek(TokenType.START)){
            return null;
        }
        try {
            lit.setBody(parseBlockStatement("FUNCTION"));
        } catch (Exception e) {
            
        }

    
        return lit;

    }

    private List<Identifier> parseFunctionParameters(){
        List<Identifier> identifiers = new ArrayList<>();

        if(peekTokenIs(TokenType.RPARA)){
            nextToken();
            return identifiers;
        }

        nextToken();

        Identifier ident = new Identifier(curToken, curToken.getLiteral());
        
        identifiers.add(ident);

        while(peekTokenIs(TokenType.COMMA)){
            nextToken();
            nextToken();
            ident = new Identifier(curToken, curToken.getLiteral());
            identifiers.add(ident);
        }
        if(!expectPeek(TokenType.RPARA)){
            return null;
        }

        return identifiers;

    }

    private BlockStatement parseBlockStatement(String type) throws Exception{
        BlockStatement bs = new BlockStatement();
        bs.setToken(curToken);

        nextToken();
        
        while(!curTokenIs(TokenType.END)){
            if(curTokenIs(TokenType.EOF)){
                endCodeError(TokenType.END);
                return null;
            }
            List<Statement> stmt = parseStatement();
            if(stmt != null){
                for(Statement statement: stmt){
                    bs.addStatement(statement);
                }
            }
            nextToken();
        }
        nextToken();
        Identifier ident = (Identifier)parseExpression(OperatorType.LOWEST.getPrecedence());
        if(type.equals("CODE") && !functionStarted){
            if(!ident.getValue().equals("CODE")){
                identifierMismatchError("CODE", ident.getValue());
                return null;
            }
            hasStarted = false;
        }else if(type.equals("FUNCTION")){
            if(!ident.getValue().equals("FUNCTION")){
                identifierMismatchError("FUNCTION", ident.getValue());
                return null;
            }
            functionStarted = false;
        }

        return bs;
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
    public Expression parseFloatLiteral(){
        
        FloatLiteral literal = new FloatLiteral();
        literal.setToken(curToken);
        float value;
        try{
            value = Float.parseFloat(curToken.getLiteral());
        }catch(NumberFormatException e){
            String msg = String.format("Could not parse %s as float", curToken.getLiteral());
            System.err.println(msg);
            errors.add(msg);
            return null;
        }

        literal.setValue(value);
        
        return literal;

    }

    public Expression parseGroupedExpression(){
        nextToken();
        Expression exp;
        try {
            exp = parseExpression(OperatorType.LOWEST.getPrecedence());

            if(!expectPeek(TokenType.RPARA)){
                return null;
            }
    
            return exp;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
        
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

        while(!peekTokenIs(TokenType.EOL) && precedence < peekPrecedence() && !peekTokenIs(TokenType.CONCAT) && !peekTokenIs(TokenType.ESCAPE) && !peekTokenIs((TokenType.COMMA))){
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
        if(Lexer.getLine() - 1 < statementsCount){
            errors.add("More than one statement per line is not allowed");
            return null;
        }
        if(!hasStarted){
            errors.add(String.format("Program should start with %s, got = %s", "BEGIN CODE", curToken.getLiteral()));
            return null;
        }
        CharStatement stmt = new CharStatement();
        stmt.setToken(curToken);

        if (!expectPeek(TokenType.IDENT)){
            return null;
        }

        if(isReservedWord(curToken.getLiteral())){
            reservedWordsError(curToken.getLiteral());
            return null;
        }
        Identifier ident = new Identifier(curToken, curToken.getLiteral());
        if(statementsList.containsKey(ident.getValue())){
            errors.add(String.format("Identifier %s is already in use", ident.getValue()));
            return null;
        }
        stmt.setName(ident);

        if(peekTokenIs(TokenType.ASSIGN)){
            nextToken();
            nextToken();
            try {
                stmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseCharStatement();
        }


        statementsList.put(ident.getValue(), stmt);
        statementsCount++;
        if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseCharStatement();
        }
        tempStatementList.add(stmt);
        return stmt;
    }


    public IntStatement parseIntStatement(){
        if(Lexer.getLine() - 1 < statementsCount){
            errors.add("More than one statement per line is not allowed");
            return null;
        }
        if(!hasStarted){
            errors.add(String.format("Program should start with %s, got = %s", "BEGIN CODE", curToken.getLiteral()));
            return null;
        }
        IntStatement stmt = new IntStatement();
        stmt.setToken(curToken);

        if (!expectPeek(TokenType.IDENT)){
            return null;
        }
            
        if(isReservedWord(curToken.getLiteral())){
            reservedWordsError(curToken.getLiteral());
            return null;
        }
        
        Identifier ident = new Identifier(curToken, curToken.getLiteral());
        if(statementsList.containsKey(ident.getValue())){
            errors.add(String.format("Identifier %s is already in use", ident.getValue()));
            return null;
        }

        stmt.setName(ident);

        if(peekTokenIs(TokenType.ASSIGN)){
            nextToken();
            nextToken();
            
            try {
                stmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
            } catch (Exception e) {
                e.printStackTrace();
            }
           
        }else if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseIntStatement();
        }
        

        statementsList.put(ident.getValue(), stmt);
        statementsCount++;
        tempStatementList.add(stmt);

        if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseIntStatement();
        }
        tempStatementList.add(stmt);
        
       
        return stmt;
    }

   
    public FloatStatement parseFloatStatement(){
        if(Lexer.getLine() -1 < statementsCount){
            errors.add("More than one statement per line is not allowed");
            return null;
        }
        if(!hasStarted){
            errors.add(String.format("Program should start with %s, got = %s", "BEGIN CODE", curToken.getLiteral()));
            return null;
        }
        FloatStatement stmt = new FloatStatement();
        stmt.setToken(curToken);

        if (!expectPeek(TokenType.IDENT)){
            return null;
        }
            
        if(isReservedWord(curToken.getLiteral())){
            reservedWordsError(curToken.getLiteral());
            return null;
        }
        
        Identifier ident = new Identifier(curToken, curToken.getLiteral());
        if(statementsList.containsKey(ident.getValue())){
            errors.add(String.format("Identifier %s is already in use", ident.getValue()));
            return null;
        }
        stmt.setName(ident);

        if(peekTokenIs(TokenType.ASSIGN)){
            nextToken();
            nextToken();
            
            try {
                stmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseFloatStatement();

        }
        
        statementsList.put(ident.getValue(), stmt);
        statementsCount++;
        if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseFloatStatement();
        }
        tempStatementList.add(stmt);
        return stmt;
    }
    
    public BoolStatement parseBoolStatement(){
        if(Lexer.getLine() - 1 < statementsCount){
            errors.add("More than one statement per line is not allowed");
            return null;
        }
        if(!hasStarted){
            errors.add(String.format("Program should start with %s, got = %s", "BEGIN CODE", curToken.getLiteral()));
            return null;
        }
        BoolStatement stmt = new BoolStatement();
        stmt.setToken(curToken);

        if (!expectPeek(TokenType.IDENT)){
            return null;
        }

        if(isReservedWord(curToken.getLiteral())){
            reservedWordsError(curToken.getLiteral());
            return null;
        }
        Identifier ident = new Identifier(curToken, curToken.getLiteral());
        if(statementsList.containsKey(ident.getValue())){
            errors.add(String.format("Identifier %s is already in use", ident.getValue()));
            return null;
        }
        stmt.setName(ident);

        if(peekTokenIs(TokenType.ASSIGN)){
            nextToken();
            nextToken();
            
            try {
                stmt.setValue(parseExpression(OperatorType.LOWEST.getPrecedence()));
            } catch (Exception e) {
                e.printStackTrace();
                }
        }else if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseBoolStatement();
        }
        

        statementsList.put(ident.getValue(), stmt);
        statementsCount++;

        if(peekTokenIs(TokenType.COMMA)){
            nextToken();
            statementsCount--;
            parseBoolStatement();
        }

        tempStatementList.add(stmt);
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

    private boolean isReservedWord(String ident){
        return reservedWords.contains(ident);
    }

    public void noPrefixParseFNError(TokenType t){
        String msg = String.format("no prefix parse function for %s found", t.getLiteral());
        errors.add(msg);
    }

    private void peekError(TokenType t){
        String msg = String.format("expected next token to be %s, got %s instead", t, peekToken.getTokenType());
        errors.add(msg);
    }

    private void identifierMismatchError(String expected, String got){
        String msg = String.format("Identifer mismatch: expected = %s, got = %s",expected,got);
        errors.add(msg);
        
    }   

    private void reservedWordsError(String ident){
        String msg = String.format("can't use reserved words as identifier, %s", ident);
        errors.add(msg);
    }

    private void endCodeError(TokenType t){
        String msg = String.format("expected token %s, got %s", t, curToken.getTokenType());
        errors.add(msg);
    }

    private void typeConversionError(TokenType t){
        String msg = String.format("Type conversion error, expected %s, got %s", t, curToken.getTokenType() );
        errors.add(msg);
    }

    private void typeConversionError(String t){
        String msg = String.format("Type conversion error, expected %s, got% s", t, curToken.getTokenType() );
        errors.add(msg);
    }


    public void registerPrefix(TokenType tokenType, PrefixParseFn fn){
        prefixParseFns.put(tokenType, fn);
    }

    public void registerInfix(TokenType tokenType, InfixParseFn fn){
        infixParseFns.put(tokenType, fn);
    }
    
}
