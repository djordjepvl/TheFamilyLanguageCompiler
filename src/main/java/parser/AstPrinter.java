package parser;

import java.util.List;
import java.util.stream.Collectors;

public final class AstPrinter implements Visitor<String> {

    // --- helpers -------------------------------------------------------------

    private static String joinChildren(Node n) {
        return n.getChildren().stream().map(c -> c.accept(new AstPrinter()))
                .collect(Collectors.joining(" "));
    }

    private static String maybe(Node n) { return (n == null) ? "" : n.accept(new AstPrinter()); }

    private static String paren(String head, String... parts) {
        String inner = String.join(" ", parts);
        inner = inner.trim();
        return "(" + head + (inner.isEmpty() ? "" : " " + inner) + ")";
    }

    // --- Program / Main / Func / Block --------------------------------------

    @Override
    public String visitProgram(ProgramNode n) {
        return paren("program", joinChildren(n));
    }

    @Override
    public String visitMain(MainNode n) {
        // očekujemo jedno dete: body (BlockNode)
        return paren("main", n.body.accept(this));
    }

    @Override
    public String visitFunc(FuncDeclNode n) {
        // Konvencija: poslednje dete je body, ostalo su parametri (VarDeclNode)
        List<Node> ch = n.getChildren();
        Node body = ch.isEmpty() ? null : ch.get(ch.size() - 1);
        List<Node> params = ch.isEmpty() ? List.of() : ch.subList(0, ch.size() - 1);

        String paramsStr = params.stream().map(p -> p.accept(this)).collect(Collectors.joining(" "));
        String head = "func " + n.name + " :ret " + n.returnKind;
        return paren(head, paren("params", paramsStr), maybe(body));
    }

    @Override
    public String visitBlock(BlockNode n) {
        return paren("block", joinChildren(n));
    }

    @Override
    public String visitArraySize(ArraySizeNode n) {
        String sizeStr = n.getChildren().get(0).accept(this); // ili n.size.accept(this)
        return "(" + "countin " + sizeStr + ")";
    }

    // --- Decls / Stmts -------------------------------------------------------

    @Override
    public String visitVarDecl(VarDeclNode n) {
        // Ako ima inicijalizaciju, deca su izrazi za = ...
        // npr: (vardecl cash a (=init 80)) ili za više deklaratora: parent ima decu VarDeclNode
        if (n.getChildren().isEmpty()) {
            return paren("vardecl", n.typeName + " " + n.name);
        } else if (n.getChildren().size() == 1) {
            String init = n.getChildren().get(0).accept(this);
            return paren("vardecl", n.typeName + " " + n.name, paren("init", init));
        } else {
            // ako si koristio "root" VarDeclNode sa decom (sledeće deklaracije),
            // ovaj visit bi se pozvao i na decu; zato ovde obuhvatimo sve:
            String all = n.getChildren().stream()
                    .map(c -> c.accept(this))
                    .collect(Collectors.joining(" "));
            return paren("vardecl", n.typeName + " " + n.name, all);
        }
    }

    @Override
    public String visitExprStmt(ExprStmtNode n) {
        String inner = n.getChildren().isEmpty() ? "" : n.getChildren().get(0).accept(this);
        return paren("stmt", inner);
    }

    @Override
    public String visitReturn(ReturnNode n) {
        // kind: "sendWord" | "sleepWithTheFishes"
        String expr = n.getChildren().isEmpty() ? "" : n.getChildren().get(0).accept(this);
        return paren("return " + n.kind, expr);
    }

    @Override
    public String visitIf(IfNode n) {
        // očekujemo: cond, thenBlock, [elseBlock]
        String cond  = n.getChildren().size() > 0 ? n.getChildren().get(0).accept(this) : "";
        String thenB = n.getChildren().size() > 1 ? n.getChildren().get(1).accept(this) : "";
        String elseB = n.getChildren().size() > 2 ? n.getChildren().get(2).accept(this) : "";
        if (elseB.isEmpty()) return paren("if", cond, thenB);
        return paren("if", cond, thenB, elseB);
    }

    @Override
    public String visitFor(ForNode n) {
        // Konvencija: 0:init?, 1:cond?, 2:step?, 3:body
        String init = n.getChildren().size() > 0 ? n.getChildren().get(0).accept(this) : "";
        String cond = n.getChildren().size() > 1 ? n.getChildren().get(1).accept(this) : "";
        String step = n.getChildren().size() > 2 ? n.getChildren().get(2).accept(this) : "";
        String body = n.getChildren().size() > 3 ? n.getChildren().get(3).accept(this) : "";
        return paren("for", paren("init", init), paren("cond", cond), paren("step", step), body);
    }

    // --- Assign / Call / LValues --------------------------------------------

    @Override
    public String visitAssign(AssignNode n) {
        // target = prvo polje; value = poslednje dete (jer prvo dete je target)
        String tgt = n.target.accept(this);
        String val = n.getChildren().isEmpty()
                ? ""
                : n.getChildren().get(n.getChildren().size() - 1).accept(this);
        return paren("=", tgt, val);
    }

    @Override
    public String visitCall(CallNode n) {
        String args = n.getChildren().stream().map(c -> c.accept(this)).collect(Collectors.joining(" "));
        return paren("call " + n.callee, args);
    }

    @Override
    public String visitIdentifier(IdentifierNode n) {
        return n.name;
    }

    @Override
    public String visitIndex(IndexNode n) {
        return paren("index", n.base.accept(this), n.index.accept(this));
    }

    // --- Literals / Ops ------------------------------------------------------

    @Override
    public String visitLiteral(LiteralNode n) {
        return n.value;
    }

    @Override
    public String visitUnary(UnaryOpNode n) {
        // očekujemo jedno dete
        String inner = n.getChildren().isEmpty() ? "" : n.getChildren().get(0).accept(this);
        return paren(n.op, inner);
    }

    @Override
    public String visitBinary(BinOpNode n) {
        // očekujemo dvoje dece
        String left  = n.getChildren().size() > 0 ? n.getChildren().get(0).accept(this) : "";
        String right = n.getChildren().size() > 1 ? n.getChildren().get(1).accept(this) : "";
        return paren(n.op, left, right);
    }
}
