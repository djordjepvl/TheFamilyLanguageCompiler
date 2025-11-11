package parser;
public class UnaryOpNode extends Node {
    public final String op;
    public UnaryOpNode(String op) { this.op = op; }
    @Override public String toString() { return super.toString() + "(" + op + ")"; }
}

