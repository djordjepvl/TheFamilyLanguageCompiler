package semantics;

import parser.*;
import java.util.*;

public class SemanticAnalyzer {

    private Scope currentScope;
    private TypeRef currentFunctionReturn = TypeRef.of(Type.VOID);
    private final FunctionTable functions = new FunctionTable();

    public void analyze(ProgramNode program) {
        currentScope = new Scope(null);
        for (Node n : program.getChildren()) {
            if (n instanceof FuncDeclNode fn) registerFunction(fn);
        }

        for (Node n : program.getChildren()) {
            analyzeNode(n);
        }
    }

    private void registerFunction(FuncDeclNode fn) {
        String name = fn.name;
        if (functions.resolve(name) != null) error("Function already declared: " + name, fn);

        TypeRef ret = TypeRef.parse(fn.returnType);

        List<TypeRef> params = new ArrayList<>();
        for (Node ch : fn.getChildren()) {
            if (ch instanceof VarDeclNode v) params.add(TypeRef.parse(v.typeName));
            else break;
        }

        functions.define(new FunctionSymbol(name, ret, params));
    }

    private void predeclareFunctions(ProgramNode program) {
        for (Node n : program.getChildren()) {
            if (n instanceof FuncDeclNode fn) {
                TypeRef ret = TypeRef.parse(fn.returnType);
                List<TypeRef> params = new ArrayList<>();
                for (Node kid : fn.getChildren()) {
                    if (kid instanceof VarDeclNode v) params.add(TypeRef.parse(v.typeName));
                    else break;
                }
                functions.define(new FunctionSymbol(fn.name, ret, params));
            }
        }
    }

    private TypeRef analyzeNode(Node n) {
        if (n instanceof ProgramNode) return analyzeProgram((ProgramNode) n);
        if (n instanceof MainNode) return analyzeMain((MainNode) n);
        if (n instanceof FuncDeclNode) return analyzeFunc((FuncDeclNode) n);
        if (n instanceof BlockNode) return analyzeBlock((BlockNode) n);
        if (n instanceof VarDeclNode) return analyzeVarDecl((VarDeclNode) n);
        if (n instanceof ExprStmtNode) return analyzeExprStmt((ExprStmtNode) n);
        if (n instanceof IfNode) return analyzeIf((IfNode) n);
        if (n instanceof ForNode) return analyzeFor((ForNode) n);
        if (n instanceof ReturnNode) return analyzeReturn((ReturnNode) n);
        if (n instanceof AssignNode) return analyzeAssign((AssignNode) n);
        if (n instanceof BinOpNode) return analyzeBinOp((BinOpNode) n);
        if (n instanceof UnaryOpNode) return analyzeUnary((UnaryOpNode) n);
        if (n instanceof LiteralNode) return analyzeLiteral((LiteralNode) n);
        if (n instanceof IdentifierNode) return analyzeIdentifier((IdentifierNode) n);
        if (n instanceof CallNode) return analyzeCall((CallNode) n);
        if (n instanceof IndexNode) return analyzeIndex((IndexNode) n);
        if (n instanceof ArraySizeNode) return analyzeArraySize((ArraySizeNode) n);
        if (n instanceof CastNode) return analyzeCast((CastNode) n);
        return TypeRef.of(Type.ERROR);
    }

    private TypeRef analyzeProgram(ProgramNode p) {
        for (Node n : p.getChildren()) analyzeNode(n);
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeMain(MainNode m) {
        TypeRef prev = currentFunctionReturn;
        currentFunctionReturn = TypeRef.of(Type.VOID);

        currentScope = new Scope(currentScope);
        for (Node kid : m.getChildren()) analyzeNode(kid);
        currentScope = currentScope.parent();

        currentFunctionReturn = prev;
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeFunc(FuncDeclNode fn) {
        TypeRef prev = currentFunctionReturn;
        currentFunctionReturn = TypeRef.parse(fn.returnType);

        currentScope = new Scope(currentScope);

        List<Node> kids = fn.getChildren();
        int i = 0;
        while (i < kids.size() && kids.get(i) instanceof VarDeclNode) {
            VarDeclNode v = (VarDeclNode) kids.get(i);
            TypeRef t = TypeRef.parse(v.typeName);
            currentScope.define(new Symbol(v.name, t));
            i++;
        }

        Node body = kids.get(kids.size() - 1);
        analyzeNode(body);

        currentScope = currentScope.parent();
        currentFunctionReturn = prev;
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeBlock(BlockNode b) {
        currentScope = new Scope(currentScope);
        for (Node n : b.getChildren()) analyzeNode(n);
        currentScope = currentScope.parent();
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeFor(ForNode f) {
        currentScope = new Scope(currentScope);
        for (Node n : f.getChildren()) analyzeNode(n);
        currentScope = currentScope.parent();
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeVarDecl(VarDeclNode v) {
        if (currentScope.isDefinedHere(v.name)) error("Variable already declared: " + v.name);
        TypeRef t = TypeRef.parse(v.typeName);
        if (t.base == Type.CREW && (t.elem == null || t.elem == Type.ERROR)) error("Crew element type missing");
        currentScope.define(new Symbol(v.name, t));

        Node init = null;

        for (Node child : v.getChildren()) {
            if (child instanceof VarDeclNode next) {
                analyzeVarDecl(next);
            } else if (child instanceof ArraySizeNode asz) {
                analyzeArraySize(asz);
            } else {
                if (init == null) init = child;
            }
        }

        if (init != null) {
            TypeRef rhs = analyzeNode(init);
            if (!assignCompatible(t, rhs)) error("Cannot assign " + rhs + " to " + t);
        }

        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeArraySize(ArraySizeNode a) {
        TypeRef sz = analyzeNode(a.size);
        if (sz.base != Type.CASH) error("Array size must be cash");
        return TypeRef.of(Type.CASH);
    }

    private TypeRef analyzeExprStmt(ExprStmtNode s) {
        analyzeNode(s.getChildren().get(0));
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeIf(IfNode i) {
        TypeRef cond = analyzeNode(i.getChildren().get(0));
        if (cond.base != Type.DOSSIER) error("If condition must be dossier");
        analyzeNode(i.getChildren().get(1));
        if (i.getChildren().size() == 3) analyzeNode(i.getChildren().get(2));
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeReturn(ReturnNode r) {
        if (r.getChildren().isEmpty()) {
            if (currentFunctionReturn.base != Type.VOID) error("Missing return value");
            return TypeRef.of(Type.VOID);
        }

        TypeRef t = analyzeNode(r.getChildren().get(0));
        if (!assignCompatible(currentFunctionReturn, t)) error("Return type mismatch");
        return TypeRef.of(Type.VOID);
    }

    private TypeRef analyzeAssign(AssignNode a) {
        if (a.target == null) error("Invalid assignment target", a);

        TypeRef rhs = analyzeNode(a.value);

        if (a.target instanceof IdentifierNode id) {
            Symbol s = currentScope.resolve(id.name);
            if (s == null) error("Variable not declared: " + id.name, id);
            if (!assignCompatible(s.type, rhs)) error("Cannot assign " + rhs + " to " + s.type, a);
            id.symbol = s;
            return s.type;
        }

        if (a.target instanceof IndexNode idx) {
            TypeRef lhs = analyzeIndex(idx);
            TypeRef s = analyzeNode(a.value);

            if (lhs.base == Type.CASH && s.base == Type.SLICE)
                error("Cannot assign slice to cash", a);
            if (!assignCompatible(lhs, rhs))
                error("Cannot assign " + s + " to " + lhs, a);
            return lhs;
        }

        error("Invalid assignment target", a);
        return TypeRef.of(Type.ERROR);
    }

    private TypeRef analyzeIdentifier(IdentifierNode i) {
        Symbol s = currentScope.resolve(i.name);
        if (s == null) error("Variable not declared: " + i.name);
        i.symbol = s;
        return s.type;
    }

    private TypeRef analyzeIndex(IndexNode idx) {
        TypeRef baseT = analyzeNode(idx.base);
        TypeRef iT = analyzeNode(idx.index);

        if (iT.base != Type.CASH) error("Index must be cash");
        if (baseT.base != Type.CREW) error("Indexing non-crew");
        if (baseT.elem == null || baseT.elem == Type.ERROR) error("Crew element type missing");

        return TypeRef.of(baseT.elem);
    }

    private TypeRef analyzeLiteral(LiteralNode l) {
        String v = l.value;
        if (v == null) return TypeRef.of(Type.ERROR);

        if (v.startsWith("$$") && v.endsWith("$$")) return TypeRef.of(Type.ASSOCIATE);

        if (v.equals("alive") || v.equals("whacked")) return TypeRef.of(Type.DOSSIER);
        if (v.length() >= 2 && v.startsWith("'") && v.endsWith("'")) return TypeRef.of(Type.MARK);
        if (v.matches("^[0-9]+$")) return TypeRef.of(Type.CASH);
        if (v.matches("^[0-9]+\\.[0-9]+$")) return TypeRef.of(Type.SLICE);

        return TypeRef.of(Type.ERROR);
    }

    private TypeRef analyzeUnary(UnaryOpNode u) {
        TypeRef t = analyzeNode(u.getChildren().get(0));
        if (u.op.equals("!")) {
            if (t.base != Type.DOSSIER) error("! expects dossier", u);
            return TypeRef.of(Type.DOSSIER);
        }
        if (u.op.equals("-")) {
            if (!t.isNumeric()) error("Unary - expects numeric", u);
            return t;
        }
        return t;
    }

    private void error(String msg, Node at) {
        throw new SemanticError(msg, null, null);
    }

    private TypeRef analyzeBinOp(BinOpNode b) {
        TypeRef l = analyzeNode(b.getChildren().get(0));
        TypeRef r = analyzeNode(b.getChildren().get(1));

        return switch (b.op) {
            case "+", "-", "*", "/" -> {
                if (!l.isNumeric() || !r.isNumeric()) error("Arithmetic on non-numeric");
                yield (l.base == Type.SLICE || r.base == Type.SLICE) ? TypeRef.of(Type.SLICE) : TypeRef.of(Type.CASH);
            }
            case "%" -> {
                if (l.base != Type.CASH || r.base != Type.CASH) error("% expects cash % cash");
                yield TypeRef.of(Type.CASH);
            }
            case "<", "<=", ">", ">=" -> {
                if (!l.isNumeric() || !r.isNumeric()) error("Relational expects numeric");
                yield TypeRef.of(Type.DOSSIER);
            }
            case "==", "!=" -> {
                if (l.isNumeric() && r.isNumeric()) yield TypeRef.of(Type.DOSSIER);
                if (!l.equals(r)) error("Equality expects same types");
                yield TypeRef.of(Type.DOSSIER);
            }
            case "&&", "||" -> {
                if (l.base != Type.DOSSIER || r.base != Type.DOSSIER) error("Logical expects dossier");
                yield TypeRef.of(Type.DOSSIER);
            }
            default -> TypeRef.of(Type.ERROR);
        };
    }

    private boolean isLValue(Node n) {
        return (n instanceof IdentifierNode) || (n instanceof IndexNode);
    }

    private TypeRef analyzeCall(CallNode c) {
        if (c.callee.equals("spill")) {
            for (Node arg : c.getChildren()) analyzeNode(arg);
            return TypeRef.of(Type.VOID);
        }

        if (c.callee.equals("snitch")) {
            if (c.getChildren().size() != 1) error("snitch expects 1 argument");
            Node arg = c.getChildren().get(0);
            if (!isLValue(arg)) error("snitch expects an lvalue");

            TypeRef t = analyzeNode(arg);
            if (t.base == Type.CREW) error("Cannot snitch a whole crew. Use index.");

            return TypeRef.of(Type.VOID);
        }

        FunctionSymbol fn = functions.resolve(c.callee);
        if (fn == null) error("Unknown function: " + c.callee);

        int got = c.getChildren().size();
        int exp = fn.arity();
        if (got != exp) error("Arity mismatch for " + c.callee);
        for (int i = 0; i < got; i++) {
            TypeRef argT = analyzeNode(c.getChildren().get(i));
            TypeRef paramT = fn.paramTypes.get(i);
            if (!assignCompatible(paramT, argT)) error("Argument type mismatch for " + c.callee);
        }

        return fn.returnType;
    }

    private TypeRef analyzeCast(CastNode c) {
        TypeRef to = TypeRef.parse(c.typeName);
        TypeRef from = analyzeNode(c.expr);

        if (!to.isNumeric()) error("Cast target must be numeric");
        if (!from.isNumeric()) error("Cannot cast non-numeric");
        if (to.base == Type.SLICE && from.base == Type.CASH) return TypeRef.of(Type.SLICE);
        if (to.base == Type.CASH && from.base == Type.SLICE) return TypeRef.of(Type.CASH);
        if (to.base == from.base) return TypeRef.of(to.base);

        error("Invalid cast");
        return TypeRef.of(Type.ERROR);
    }

    private boolean assignCompatible(TypeRef left, TypeRef right) {
        if (left.equals(right)) return true;
        if (left.base == Type.SLICE && right.base == Type.CASH) return true;
        return false;
    }

    private void error(String msg) {
        throw new SemanticError(msg, null, null);
    }
}