package object;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, Object> store;

    public Environment() {
        this.store = new HashMap<>();
    }

    public Object get(String name){
        return store.get(name);
    }

    public boolean has(String name){
        return store.containsKey(name);
    }

    public Object set(String name, Object value){
        store.put(name, value);
        return value;

    }
    
    
    
}
