package semantics;

import java.util.Objects;

public final class TypeRef {
    public final Type base;
    public final Type elem;

    private TypeRef(Type base, Type elem) {
        this.base = base;
        this.elem = elem;
    }

    public static TypeRef of(Type base) {
        return new TypeRef(base, null);
    }

    public static TypeRef crew(Type elem) {
        return new TypeRef(Type.CREW, elem);
    }

    public static TypeRef parse(String tflType) {
        if (tflType == null) return TypeRef.of(Type.ERROR);
        String s = tflType.trim();
        if (s.startsWith("crew[") && s.endsWith("]")) {
            String inner = s.substring("crew[".length(), s.length() - 1).trim();
            Type e = Type.fromTflString(inner);
            return TypeRef.crew(e);
        }
        return TypeRef.of(Type.fromTflString(s));
    }

    public boolean isNumeric() {
        return base == Type.CASH || base == Type.SLICE;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof TypeRef t)) return false;
        return base == t.base && elem == t.elem;
    }

    @Override public int hashCode() {
        return Objects.hash(base, elem);
    }

    @Override public String toString() {
        if (base != Type.CREW) return String.valueOf(base);
        return "CREW[" + elem + "]";
    }
}
