package parser;

public final class AssignNode extends Node {
    public final Node target; // lvalue: IdentifierNode ili IndexNode lanac
    public AssignNode(Node target){
        this.target = target;
        add(target); // ← važno: da se vidi u outline-u kao prvo dete
    }
    @Override public <R> R accept(Visitor<R> v){ return v.visitAssign(this); }

    @Override protected void toJsonFields(java.util.Map<String, Object> out) {
        out.put("kind", "assign");
    }
}