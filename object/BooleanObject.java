package object;

public class BooleanObject implements Object{
    private boolean value;

    public BooleanObject(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String inspect() {
        if(value == true){
            return "TRUE";
        }
        return "FALSE";
    }

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN_OBJ;
    }

    
}
