package object;

public enum ObjectType {

    INTEGER_OBJ("INTEGER"),
    BOOLEAN_OBJ("BOOLEAN"),
    CHARACTER_OBJ("CHARACTER"),
    NULL_OBJ("NULL");

    private String name;

    private ObjectType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    

}
