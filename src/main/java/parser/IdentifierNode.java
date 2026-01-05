package parser;

import semantics.Symbol;

public final class IdentifierNode extends Node {
    public final String name;
    public Symbol symbol;

    public IdentifierNode(String name){ this.name = name; }

    @Override
    public <R> R accept(Visitor<R> v){ return v.visitIdentifier(this); }

    @Override
    protected void toJsonFields(java.util.Map<String,Object> out){
        out.put("name", name);
    }

    @Override
    protected String debugLabel(){
        return super.debugLabel() + "(" + name + ")";
    }
}
