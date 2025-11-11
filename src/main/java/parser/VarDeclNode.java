package parser;
public class VarDeclNode extends Node {
    public final String type;
    public final String name;
    public VarDeclNode(String type, String name) { this.type = type; this.name = name; }
    @Override public String toString() { return super.toString() + "(" + type + " " + name + ")"; }
}

