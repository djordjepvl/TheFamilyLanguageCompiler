package parser;

public final class ReturnNode extends Node {
    public final String kind; // "sendWord" | "sleepWithTheFishes"
    public ReturnNode(String kind){ this.kind = kind; }
    @Override public <R> R accept(Visitor<R> v){ return v.visitReturn(this); }

    @Override protected void toJsonFields(java.util.Map<String,Object> out){ out.put("kind", kind); }
    @Override protected String debugLabel(){ return super.debugLabel() + "(" + kind + ")"; }
}
