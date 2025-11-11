package parser;

public class ForNode extends Node{
    @Override public <R> R accept(Visitor<R> v) { return v.visitFor(this); }
}
