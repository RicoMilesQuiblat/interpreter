package object;

public enum ObjectType {

    INTEGER_OBJ("INTEGER"),
    FLOAT_OBJ("FLOAT"),
    BOOLEAN_OBJ("BOOLEAN"),
    CHARACTER_OBJ("CHARACTER"),
    NULL_OBJ("NULL"),
    ERROR_OBJ("ERROR"),
    BEGIN_OBJ("BEGIN"),
    STRUCTURE_OBJ("STRUCTURE"),
    RETURN_VALUE_OBJ("RETURN_VALUE"),
    FUNCTION_OBJ("FUNCTION"),
    STRING_OBJ("STRING"),
    HASH_OBJ("HASH");

    private String name;

    private ObjectType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    

}
