package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import ast.BoolStatement;
import ast.CharStatement;
import ast.Expression;
import ast.ExpressionStatement;
import ast.Identifier;
import ast.InfixExpression;
import ast.IntegerLiteral;
import ast.Node;
import ast.PrefixExpression;
import ast.Program;
import ast.Statement;
import lexer.Lexer;


public class ParserTest {

    @org.junit.Test
    public static void testIdentifierExpression() throws Exception{
        String input = "imissher$";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.ParseProgram();
        p.checkParserErrors();

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);
        
        ExpressionStatement expressionStatement = (ExpressionStatement) stmt;
        Expression expression = expressionStatement.getExpression();
        assertTrue(expression instanceof Identifier);
        
        Identifier identifier = (Identifier) expression;
        assertEquals("imissher", identifier.getValue());
        assertEquals("imissher", identifier.getTokenLiteral());
    }

    @org.junit.Test
    public static void testIntegerLiteralExpression() throws Exception{
        String input = "5$";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.ParseProgram();
        p.checkParserErrors();

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);
        
        ExpressionStatement expressionStatement = (ExpressionStatement) stmt;
        Expression expression = expressionStatement.getExpression();
        assertTrue(expression instanceof IntegerLiteral);
        
        IntegerLiteral identifier = (IntegerLiteral) expression;
        assertEquals(5, identifier.getValue());
        assertEquals("5", identifier.getTokenLiteral());
    }

    @org.junit.Test
    public static void testParsingPrefixExpressions() throws Exception{
        List<PrefixTestCase> prefixTests = new ArrayList<>();
        prefixTests.add(new PrefixTestCase("-15", "-", 15));

        for (PrefixTestCase pt : prefixTests){
            Lexer lexer = new Lexer(pt.getInput());
            Parser p = new Parser(lexer);
            Program program = p.ParseProgram();
            p.checkParserErrors();

            assertEquals(1, program.getStatements().size());

            Statement stmt = program.getStatements().get(0);
            assertTrue(stmt instanceof ExpressionStatement);

            ExpressionStatement expressionStatement = (ExpressionStatement) stmt;
            Expression expression = expressionStatement.getExpression();
            assertTrue(expression instanceof PrefixExpression);

            PrefixExpression prexp = (PrefixExpression) expression;
            assertEquals(pt.getOperator(), prexp.getOperator());

            
        }

    }

    @org.junit.Test
    public static void testParsingInfixExpressions() throws Exception{
        List<InfixTestCase> infixTests = new ArrayList<>();
        infixTests.add(new InfixTestCase("5 + 5",5, "+", 5));
        infixTests.add(new InfixTestCase("5 - 5",5, "-", 5));
        infixTests.add(new InfixTestCase("5 * 5",5, "*", 5));
        infixTests.add(new InfixTestCase("5 / 5",5, "/", 5));
        infixTests.add(new InfixTestCase("5 % 5",5, "%", 5));
        infixTests.add(new InfixTestCase("5 < 5",5, "<", 5));
        infixTests.add(new InfixTestCase("5 > 5",5, ">", 5));
        infixTests.add(new InfixTestCase("5 <= 5",5, "<=", 5));
        infixTests.add(new InfixTestCase("5 >= 5",5, ">=", 5));
        infixTests.add(new InfixTestCase("5 == 5",5, "==", 5));
        infixTests.add(new InfixTestCase("5 <> 5",5, "<>", 5));
        

        for (InfixTestCase it : infixTests){
            Lexer lexer = new Lexer(it.getInput());
            Parser p = new Parser(lexer);
            Program program = p.ParseProgram();
            p.checkParserErrors();

            assertEquals(1, program.getStatements().size());

            Statement stmt = program.getStatements().get(0);
            assertTrue(stmt instanceof ExpressionStatement);

            ExpressionStatement expressionStatement = (ExpressionStatement) stmt;
            Expression expression = expressionStatement.getExpression();
            assertTrue(expression instanceof InfixExpression);

            InfixExpression exp = (InfixExpression) expression;
            
            if(!testIntegerLiteral(exp.getLeft(), it.getLeftValue())){
                return;
            }

            assertEquals(it.getOperator(), exp.getOperator());

            if(!testIntegerLiteral(exp.getRight(), it.getRightValue())){
                return;
            }

            
        }

    }

    @org.junit.Test
    public static boolean testIntegerLiteral(Expression ex, int value){
        IntegerLiteral integer = (IntegerLiteral) ex;
        assertTrue(integer instanceof IntegerLiteral);

        assertEquals(integer.getValue(), value);
        assertEquals(integer.getTokenLiteral(), Integer.toString(value));

        return true;
    }


    @org.junit.Test
    public static void testStatements() throws Exception{
        System.out.println("INT TEST");
        String input =
                "BOOL x  = \"TRUE\"$\n" +
                        "BOOL y = \"FALSE\"$\n" +
                        "BOOL z =\"TRUE\"$\n";
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


    private static class PrefixTestCase{
        private String input;
        private String operator;
        private int integerValue;

        public PrefixTestCase(String input, String operator, int integerValue) {
            this.input = input;
            this.operator = operator;
            this.integerValue = integerValue;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public int getIntegerValue() {
            return integerValue;
        }

        public void setIntegerValue(int integerValue) {
            this.integerValue = integerValue;
        }

        
        
    }
    private static class InfixTestCase{
        private String input;
        private int leftValue;
        private String operator;
        private int rightValue;
        
        public InfixTestCase(String input, int leftValue, String operator, int rightValue) {
            this.input = input;
            this.leftValue = leftValue;
            this.operator = operator;
            this.rightValue = rightValue;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public int getLeftValue() {
            return leftValue;
        }

        public void setLeftValue(int leftValue) {
            this.leftValue = leftValue;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public int getRightValue() {
            return rightValue;
        }

        public void setRightValue(int rightValue) {
            this.rightValue = rightValue;
        }

       

        
        
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
