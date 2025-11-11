package parser;
public final class LiteralNode extends Node {
    public final String value; // "80", "'A'", "$$text$$", "alive"…
    public LiteralNode(String value){ this.value = value; }
    @Override public <R> R accept(Visitor<R> v){ return v.visitLiteral(this); }
    @Override protected void toJsonFields(java.util.Map<String,Object> out){ out.put("value", value); }
    @Override protected String debugLabel(){ return super.debugLabel() + "(" + value + ")"; }
}

