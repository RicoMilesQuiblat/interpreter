package token;

public enum TokenType {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    //Identifiers + literals
    IDENT("IDENT"),
    INTEGER("INTEGER"),
    FLOATINGPOINT("FLOATINGPOINT"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    CHARACTER("CHARACTER"),

    //Operators
    ASSIGN("="),
    LPARA("("),
    RPARA(")"),
    PLUS("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
    GREAT(">"),
    LESS("<"),
    GREATEQ(">="),
    LESSEQ("<="),
    EQUAL("=="),
    NOTEQUAL("<>"),
    ESCAPE("["),



    //KEYWORD
    START("BEGIN"),
    END("END"),
    INT("INT"),
    CHAR("CHAR"),
    BOOL("BOOL"),
    FLOAT("FLOAT"),
    DISPLAY("DISPLAY"),
    SCAN("SCAN"),
    FUNCTION("FUNCTION"),
    IF("IF"),
    ELSE("ELSE"),
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    STRING("STRING"),
    WHILE("WHILE"),

    
    //SPECIAL
    COMMENT("#"),
    COLON(":"),
    COMMA(","),
    EOL("$"),
    CONCAT("&");



    private final String literal;

    private TokenType(String literal){
        this.literal = literal;
    }

    public String getLiteral(){
        return literal;
    }
}