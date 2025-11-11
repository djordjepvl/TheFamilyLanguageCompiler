package parser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Node {
    private static final AtomicInteger GEN = new AtomicInteger(1);

    public final int id = GEN.getAndIncrement();

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
        toJson(sb);
        return sb.toString();
    }
    private void toJson(StringBuilder sb) {
        sb.append("{\"type\":\"").append(getClass().getSimpleName())
                .append("\",\"id\":").append(id);

        // hook za podklase – dopunska polja (npr. name/op/value)
        Map<String, Object> meta = new LinkedHashMap<>();
        toJsonFields(meta);
        if (!meta.isEmpty()) {
            for (Map.Entry<String,Object> e : meta.entrySet()) {
                sb.append(",\"").append(e.getKey()).append("\":");
                writeJsonValue(sb, e.getValue());
            }
        }

        sb.append(",\"children\":[");
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) sb.append(",");
            children.get(i).toJson(sb);
        }
        sb.append("]}");
    }

    /** Podklase mogu da upišu dodatna JSON polja ovde (default: ništa). */
    protected void toJsonFields(Map<String, Object> out) { /* no-op */ }

    /** Jednostavan JSON writer za osnovne tipove */
    private static void writeJsonValue(StringBuilder sb, Object v) {
        if (v == null) { sb.append("null"); return; }
        if (v instanceof Number || v instanceof Boolean) { sb.append(v); return; }
        sb.append("\"").append(escape(String.valueOf(v))).append("\"");
    }
    private static String escape(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");
    }

    /** Možeš po želji da vratiš bogatiji label (npr. uključiti ime/op/vrednost) */
    protected String debugLabel() {
        return getClass().getSimpleName() + "#" + id;
    }

    @Override public String toString() { return debugLabel(); }

    /** Visitor hook – obavezno u svakoj podklasi implementirati visitX poziv. */
    public abstract <R> R accept(Visitor<R> v);
}