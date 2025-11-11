package parser;

import parser.Node;

public class FuncDeclNode extends Node {
    public final String name;
    public final String returnKind; // "forgetAboutIt" ili konkretan tip
    public FuncDeclNode(String returnKind, String name){ this.returnKind = returnKind; this.name = name; }
    @Override public <R> R accept(Visitor<R> v) { return v.visitFunc(this); }
}

