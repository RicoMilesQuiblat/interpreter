package parser;

import java.util.ArrayList;
import java.util.List;

import ast.BeginStatement;
import ast.BoolStatement;
import ast.CharStatement;
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

    public Parser(Lexer lexer){
        this.lexer = lexer;
        errors = new ArrayList<>();
        nextToken();
        nextToken();

    }


    public void nextToken(){
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public Program ParseProgram(){
        
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

    public Statement parseStatement() {
            switch (curToken.getTokenType()){
                case CHAR:
                    return parseCharStatement();
                case INT:
                    return parseIntStatement();
                case BOOL:
                    return parseBoolStatement();

                default:
                return null;
                
            }
        
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
    
}
