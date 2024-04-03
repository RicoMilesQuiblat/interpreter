package object;

public enum ObjectType {

    INTEGER_OBJ("INTEGER"),
    FLOAT_OBJ("FLOAT"),
    BOOLEAN_OBJ("BOOLEAN"),
    CHARACTER_OBJ("CHARACTER"),
    NULL_OBJ("NULL"),
    ERROR_OBJ("ERROR"),
    BEGIN_OBJ("BEGIN"),
    STRUCTURE_OBJ("STRUCTURE");

    private String name;

    private ObjectType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    

}
