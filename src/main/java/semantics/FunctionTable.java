package semantics;

import java.util.HashMap;
import java.util.Map;

public final class FunctionTable {
    private final Map<String, FunctionSymbol> fns = new HashMap<>();

    public void define(FunctionSymbol fn) {
        if (fns.containsKey(fn.name)) {
            throw new SemanticError("Function already declared: " + fn.name, null, null);
        }
        fns.put(fn.name, fn);
    }

    public FunctionSymbol resolve(String name) {
        return fns.get(name);
    }
}
