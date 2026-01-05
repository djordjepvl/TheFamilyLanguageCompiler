package parser;

import java.util.Map;

public final class CastNode extends Node {
    public final String typeName;
    public final Node expr;

    public CastNode(String typeName, Node expr) {
        this.typeName = typeName;
        this.expr = expr;
        add(expr);
    }

    @Override
    public <R> R accept(Visitor<R> v) { return v.visitCast(this); }

    @Override
    protected void toJsonFields(Map<String, Object> out) {
        out.put("typeName", typeName);
    }

    @Override
    protected String debugLabel() {
        return super.debugLabel() + "(" + typeName + ")";
    }
}
