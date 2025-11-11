package parser;
public class IdentifierNode extends Node {
    public final String name;
    public IdentifierNode(String name) { this.name = name; }
    @Override public String toString() { return super.toString() + "(" + name + ")"; }
}

