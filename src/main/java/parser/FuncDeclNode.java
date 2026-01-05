package parser;

public class FuncDeclNode extends Node {
    public final String name;
    public final String returnKind;
    public final String returnType;

    public FuncDeclNode(String returnKind, String name) {
        this.returnKind = returnKind;
        this.returnType = returnKind;
        this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> v) { return v.visitFunc(this); }
}
