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
    RESCAPE("]"),
    LESCAPE("["),



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
    
    //SPECIAL
    COMMENT("#"),
    COLON(":"),
    COMMA(","),
    EOL("$");



    private final String literal;

    private TokenType(String literal){
        this.literal = literal;
    }

    public String getLiteral(){
        return literal;
    }
}