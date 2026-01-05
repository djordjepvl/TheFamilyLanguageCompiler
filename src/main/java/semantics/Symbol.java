package semantics;

public class Symbol {
    public final String name;
    public final TypeRef type;

    public Symbol(String name, TypeRef type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public TypeRef getType() { return type; }
}
