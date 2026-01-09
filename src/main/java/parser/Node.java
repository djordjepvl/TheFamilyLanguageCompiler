package parser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Node {
    private static final AtomicInteger GEN = new AtomicInteger(1);

    public final int id = GEN.getAndIncrement();

    private String type;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    protected final List<Node> children = new ArrayList<>();

    // --- child helpers ---
    public void add(Node n) { if (n != null) children.add(n); }
    public void addAll(Collection<? extends Node> ns) { if (ns != null) ns.forEach(this::add); }

    /** Nezamenljivi pogled na decu (immutable view) */
    public List<Node> getChildren() { return Collections.unmodifiableList(children); }

    /** (opciono) zamena deteta – zgodno za transformacije */
    public void replaceChild(int idx, Node with) {
        if (idx < 0 || idx >= children.size()) throw new IndexOutOfBoundsException();
        children.set(idx, with);
    }

    

    // --- pretty print ---
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printTree(sb, 0);
        return sb.toString();
    }
    private void printTree(StringBuilder sb, int depth) {
        sb.append("  ".repeat(depth)).append(debugLabel()).append("\n");
        for (Node c : children) c.printTree(sb, depth + 1);
    }

    // --- JSON ---
    public final String toJson() {
        StringBuilder sb = new StringBuilder();
        toJson(sb, 0); // Start the recursive process
        return sb.toString();
    }

    // 1. Ensure this takes both StringBuilder AND indent
    protected void toJson(StringBuilder sb, int indent) {
        String margin = "  ".repeat(indent);
        String childMargin = "  ".repeat(indent + 1);

        sb.append("{\n");
        sb.append(childMargin).append("\"type\": \"").append(getClass().getSimpleName()).append("\",\n");
        sb.append(childMargin).append("\"id\": ").append(id);

        Map<String, Object> meta = new java.util.LinkedHashMap<>();
        toJsonFields(meta);
        for (var entry : meta.entrySet()) {
            sb.append(",\n").append(childMargin).append("\"").append(entry.getKey()).append("\": ");
            writeJsonValue(sb, entry.getValue(), indent + 1);
        }

        sb.append(",\n").append(childMargin).append("\"children\": [");
        if (!children.isEmpty()) {
            sb.append("\n");
            for (int i = 0; i < children.size(); i++) {
                sb.append(childMargin).append("  ");
                children.get(i).toJson(sb, indent + 2); // Correct recursive call
                if (i < children.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(childMargin);
        }
        sb.append("]\n").append(margin).append("}");
    }

    /** Podklase mogu da upišu dodatna JSON polja ovde (default: ništa). */
    protected void toJsonFields(Map<String, Object> out) { /* no-op */ }

    /** Jednostavan JSON writer za osnovne tipove */
    private void writeJsonValue(StringBuilder sb, Object value, int indent) {
        if (value instanceof String) {
            sb.append("\"").append(value).append("\"");
        } else if (value instanceof Node) {
            ((Node) value).toJson(sb, indent); // Now matches the signature above
        } else {
            sb.append(value);
        }
    }
    private static String escape(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");
    }

    /** Možeš po želji da vratiš bogatiji label (npr. uključiti ime/op/vrednost) */
    protected String debugLabel() {
        String label = getClass().getSimpleName() + "#" + id;
        if (type != null) {
            label += " [" + type + "]";
        }
        return label;
    }

    @Override public String toString() { return debugLabel(); }

    /** Visitor hook – obavezno u svakoj podklasi implementirati visitX poziv. */
    public abstract <R> R accept(Visitor<R> v);
}