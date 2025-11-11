package parser;

public class IfNode extends Node{
    @Override public <R> R accept(Visitor<R> v){ return v.visitIf(this); }
}
