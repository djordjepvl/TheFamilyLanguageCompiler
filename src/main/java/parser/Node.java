package parser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Node {
    private static final AtomicInteger GEN = new AtomicInteger(1);
    public final int id = GEN.getAndIncrement();
    protected final List<Node> children = new ArrayList<>();

    public void add(Node n) { if (n != null) children.add(n); }
    public List<Node> children() { return Collections.unmodifiableList(children); }

    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printTree(sb, 0);
        return sb.toString();
    }
    private void printTree(StringBuilder sb, int depth) {
        sb.append("  ".repeat(depth)).append(this).append("\n");
        for (Node c : children) c.printTree(sb, depth + 1);
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        toJson(sb);
        return sb.toString();
    }
    private void toJson(StringBuilder sb) {
        sb.append("{\"type\":\"").append(getClass().getSimpleName())
                .append("\",\"id\":").append(id).append(",\"children\":[");
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) sb.append(",");
            children.get(i).toJson(sb);
        }
        sb.append("]}");
    }

    @Override public String toString() {
        return getClass().getSimpleName() + "#" + id;
    }
}
