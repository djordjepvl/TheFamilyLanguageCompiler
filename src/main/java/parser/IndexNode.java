package parser;

public class IndexNode extends Node{
    public final Node base;
    public final Node index;
    public IndexNode(Node base, Node index) { this.base = base; this.index = index; add(base); add(index); }
    @Override public <R> R accept(Visitor<R> v) { return v.visitIndex(this); }
}
