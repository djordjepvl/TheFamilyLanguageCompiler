package parser;

public class BlockNode extends Node{
    @Override public <R> R accept(Visitor<R> v){ return v.visitBlock(this); }
}
