package parser;
public class BinOpNode extends Node {
    public final String op;
    public BinOpNode(String op) { this.op = op; }
    @Override public String toString() { return super.toString() + "(" + op + ")"; }
}

