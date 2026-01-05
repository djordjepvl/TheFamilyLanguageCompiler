package semantics;

import java.util.List;

public final class FunctionSymbol {
    public final String name;
    public final TypeRef returnType;
    public final List<TypeRef> paramTypes;

    public FunctionSymbol(String name, TypeRef returnType, List<TypeRef> paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = List.copyOf(paramTypes);
    }

    public int arity() { return paramTypes.size(); }
}
