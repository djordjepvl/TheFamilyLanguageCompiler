package parser;

public final class CallNode extends Node {
    public final String callee; // npr. "spill" ili "foo"
    public CallNode(String callee){ this.callee = callee; }
    @Override public <R> R accept(Visitor<R> v){ return v.visitCall(this); }
    @Override protected void toJsonFields(java.util.Map<String,Object> out){ out.put("callee", callee); }
    @Override protected String debugLabel(){ return super.debugLabel() + "(" + callee + ")"; }
}