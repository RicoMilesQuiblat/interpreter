package lexer;

import java.util.Arrays;
import java.util.List;

import token.Token;
import token.TokenType;

public class LexerTest {
    
    public static void main(String[] args){
        String input = "BEGIN CODE\n    INT num = 5\n CHAR ch = \'c\' \n #comment bruh \n BOOL bl = \"TRUE\" \n END CODE";

        List<TestToken> tests = Arrays.asList(
            new TestToken(TokenType.START, "BEGIN CODE"),
            new TestToken(TokenType.INT, "INT"),
            new TestToken(TokenType.IDENT, "num"),
            new TestToken(TokenType.ASSIGN, "="),
            new TestToken(TokenType.DIGIT, "5"),
            new TestToken(TokenType.CHAR, "CHAR"),
            new TestToken(TokenType.IDENT, "ch"),
            new TestToken(TokenType.ASSIGN, "="),
            new TestToken(TokenType.CHARACTER, "c"),
            new TestToken(TokenType.COMMENT, "#comment bruh "),
            new TestToken(TokenType.BOOL, "BOOL"),
            new TestToken(TokenType.IDENT, "bl"),
            new TestToken(TokenType.ASSIGN, "="),
            new TestToken(TokenType.BOOLEAN, "TRUE"),
            new TestToken(TokenType.END, "END CODE")
        );

        Lexer lexer = new Lexer(input);

        

        for (int i = 0; i < tests.size(); i++){
            TestToken tt = tests.get(i);
            Token tok = lexer.nextToken();

            System.out.println(String.format("test[%s]",i));

            if(tok.getTokenType() != tt.getExpectedType()){
                throw new AssertionError(String.format("test[%d] - token type wrong. expected= %s, got= %s",i,tt.getExpectedType(),tok.getTokenType()));
            }else{
                System.out.println(String.format("Token Type = %s", tok.getTokenType()));
            }

            if (!tok.getLiteral().equals(tt.getExpectedLiteral())) {
                throw new AssertionError(String.format("tests[%d] - literal wrong. expected= %s, got= %s", i, tt.getExpectedLiteral(), tok.getLiteral()));
            }else {
                System.out.println(String.format("Literal = %s", tok.getLiteral()));
            }
            System.out.println("");
        }
    }
    static class TestToken {
        private TokenType expectedType;
        private String expectedLiteral;
        
        public TestToken(TokenType expectedType, String expectedLiteral){
            this.expectedType = expectedType;
            this.expectedLiteral = expectedLiteral;
        }
        
        public TokenType getExpectedType(){
            return expectedType;
        }
        
        public String getExpectedLiteral(){
            return expectedLiteral;
        }
    }
}
