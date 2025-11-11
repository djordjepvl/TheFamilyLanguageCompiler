package parser;

import java.util.Map;

public final class ArraySizeNode extends Node {
    public final Node size;

    public ArraySizeNode(Node size) {   // ✅ ispravno ime konstruktora
        this.size = size;
        add(size);                      // da se vidi u outline-u/JSON-u
    }

    @Override
    public <R> R accept(Visitor<R> v) { return v.visitArraySize(this); }

    @Override
    protected void toJsonFields(Map<String, Object> out) {
        out.put("kind", "countin");
    }

    @Override
    protected String debugLabel() { return super.debugLabel() + "(countin)"; }
}