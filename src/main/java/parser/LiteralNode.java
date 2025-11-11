package parser;
public class LiteralNode extends Node {
    public final String lexeme;
    public LiteralNode(String lexeme) { this.lexeme = lexeme; }
    @Override public String toString() { return super.toString() + "(" + lexeme + ")"; }
}

