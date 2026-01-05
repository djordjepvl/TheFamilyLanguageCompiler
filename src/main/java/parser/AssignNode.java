package parser;

public final class AssignNode extends Node {
    public final Node target;
    public final Node value;   // ✅ RHS
    public final String name;

    public AssignNode(Node target, Node value) {
        this.target = target;
        this.value = value;
        add(target);
        add(value);

        if (target instanceof IdentifierNode id) this.name = id.name;
        else this.name = null;
    }

    @Override
    public <R> R accept(Visitor<R> v){ return v.visitAssign(this); }
}

