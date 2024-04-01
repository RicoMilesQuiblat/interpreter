package object;

public class BeginObject implements Object{

    @Override
    public String inspect() {
        return "BEGIN CODE";
    }

    @Override
    public ObjectType type() {
        return ObjectType.BEGIN_OBJ;
    }
    
    
}
