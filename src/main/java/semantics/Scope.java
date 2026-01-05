package semantics;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope parent;
    private final Map<String, Symbol> vars = new HashMap<>();

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public Scope parent() { return parent; }

    public boolean isDefinedHere(String name) {
        return vars.containsKey(name);
    }

    public void define(Symbol s) {
        vars.put(s.name, s);
    }

    public Symbol resolve(String name) {
        if (vars.containsKey(name)) return vars.get(name);
        if (parent != null) return parent.resolve(name);
        return null;
    }
}
