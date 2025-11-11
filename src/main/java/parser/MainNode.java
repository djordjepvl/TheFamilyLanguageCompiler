package parser;

public class MainNode extends Node {
    public final BlockNode body;
    public MainNode(BlockNode body) { this.body = body; add(body); }
    @Override
    public <R> R accept(Visitor<R> v) { return v.visitMain(this); }
}
