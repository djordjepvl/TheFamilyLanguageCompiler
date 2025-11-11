package parser;
public final class BinOpNode extends Node {
    public final String op;
    public BinOpNode(String op){ this.op = op; }
    @Override public <R> R accept(Visitor<R> v){ return v.visitBinary(this); }
    @Override protected void toJsonFields(java.util.Map<String,Object> out){ out.put("op", op); }
    @Override protected String debugLabel(){ return super.debugLabel() + "(" + op + ")"; }
}

