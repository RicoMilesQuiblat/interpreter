package token;

public enum TokenType {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    //Identifiers + literals
    IDENT("IDENT"),
    DIGIT("DIGIT"),

    //Operators
    ASSIGN("="),
    PLUS("+"),
    SUBTRACT("-"),

    //KEYWORD
    START("BEGIN CODE"),
    END("END CODE"),
    INT("INT"),
    CHAR("CHAR"),
    BOOL("BOOL"),
    FLOAT("FLOAT"),
    COMMENT("#");



    private final String literal;

    private TokenType(String literal){
        this.literal = literal;
    }

    public String getLiteral(){
        return literal;
    }
}