package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import java.util.ArrayList;
import java.util.List;

import ast.BoolStatement;
import ast.CharStatement;
import ast.Node;
import ast.Program;
import ast.Statement;
import junit.framework.Test;
import lexer.Lexer;


public class ParserTest {

    
    @org.junit.Test
    public static void testStatements() throws Exception{
        System.out.println("INT TEST");
        String input = "BOOL x   \"TRUE\"$\n" +
                        "BOOL  = \"FALSE\"$\n" +
                        "BOOL  \"TRUE\"$\n";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.ParseProgram();
        parser.checkParserErrors();

        System.out.println("Parsing");

       assertNotNull("ParseProgram() returned null", program);
       assertEquals("program.Statements does not contain 3 statements",3, program.getStatements().size());

       List<TestCase> tests = new ArrayList<>();
       tests.add(new TestCase("x"));
       tests.add(new TestCase("y"));
       tests.add(new TestCase("z"));

       for(int i = 0; i < tests.size(); i++){
            Statement stmt = program.getStatements().get(i);
            if(!testStatement(stmt, tests.get(i))){
                return;
            }
       }

    }


    
    private static boolean testStatement(Statement s, TestCase tc){
        if(!(s.getTokenLiteral().equals("BOOL"))){
            System.err.println("s.TokenLiteral not 'BOOL' . got = " + ( s.getTokenLiteral()));
            return false;
        }else{
            System.out.println(String.format("Token Literal = %s ", s.getTokenLiteral()));
        }

        if(!(s instanceof BoolStatement)){
            System.err.println("S NOT *INTStatement. got = " + s.getClass().getName());
            return false;
        }else {
            System.out.println("enter");
            System.out.println(String.format("%s is an instance of INTStatement ", s.getClass().getName()));
        }

        BoolStatement charStmt = (BoolStatement)s;
        if(!charStmt.getName().getValue().equals(tc.getExpectedIdentifier())){
            System.err.println("intStmt.Value not '" + tc.getExpectedIdentifier() + "'. got = " + charStmt.getValue());
            return false;
        }else  {
            System.out.println(String.format("Value = ", charStmt.getValue()));
        }

        if(!charStmt.getName().getTokenLiteral().equals(tc.expectedIdentifier)){
            System.err.println("intStmt.Value not '" + tc.getExpectedIdentifier() + "'. got = " + charStmt.getTokenLiteral());
            return false;
        }else {
            System.out.println(String.format("Literal = ", charStmt.getTokenLiteral()));
        }
        return true;
    }


    private static class TestCase {
        private String expectedIdentifier;

        public TestCase(String expectedIdentifier){
            this.expectedIdentifier = expectedIdentifier;
        }

        public String getExpectedIdentifier() {
            return expectedIdentifier;
        }

        public void setExpectedIdentifier(String expectedIdentifier) {
            this.expectedIdentifier = expectedIdentifier;
        }

        
    }
}
