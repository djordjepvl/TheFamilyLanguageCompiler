package parser;

public final class VarDeclNode extends Node {
    public final String typeName; // npr. "cash" ili "crew[cash]"
    public final String name;     // npr. "a"
    public VarDeclNode(String typeName, String name){
        this.typeName = typeName; this.name = name;
    }
    @Override public <R> R accept(Visitor<R> v){ return v.visitVarDecl(this); }

    @Override protected void toJsonFields(java.util.Map<String, Object> out) {
        out.put("type", typeName);
        out.put("name", name);
    }
    @Override protected String debugLabel() {
        return super.debugLabel() + "(" + typeName + " " + name + ")";
    }
}