package object;

public enum ObjectType {

    INTEGER_OBJ("INTEGER"),
    BOOLEAN_OBJ("BOOLEAN"),
    CHARACTER_OBJ("CHARACTER"),
    NULL_OBJ("NULL"),
    ERROR_OBJ("ERROR"),
    BEGIN_OBJ("BEGIN");

    private String name;

    private ObjectType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    

}
