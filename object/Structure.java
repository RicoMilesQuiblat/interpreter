package object;

import ast.BlockStatement;

public class Structure implements Object {
    BlockStatement body;

    public Structure(BlockStatement body) {
        this.body = body;
    }

    @Override
    public String inspect() {
        StringBuilder out = new StringBuilder();

        out.append("BEGIN CODE");
        out.append("\n");
        out.append(body.string());
        out.append("\n");
        out.append("END CODDE");

        return out.toString();
    }

    @Override
    public ObjectType type() {
        return ObjectType.STRUCTURE_OBJ;
    }

    
}
