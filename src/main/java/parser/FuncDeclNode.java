package parser;

import parser.Node;

public class FuncDeclNode extends Node {
    public final String retType;
    public final String name;
    public FuncDeclNode(String retType, String name) { this.retType = retType; this.name = name; }
    @Override public String toString() { return super.toString() + "(" + retType + " " + name + ")"; }
}

