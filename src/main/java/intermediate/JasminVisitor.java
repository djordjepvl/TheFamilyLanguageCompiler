package intermediate;

import parser.*;

public class JasminVisitor implements Visitor<String> {

    private String className;
    public JasminVisitor(String className) {
        this.className = className;
    }

    @Override
    public String visitProgram(ProgramNode n) {
        String header = ".class public " + this.className + "\n"
                + ".super java/lang/Object\n\n";

        String constructor = ".method public <init>()V\n"
                + "    aload_0\n"
                + "    invokespecial java/lang/Object/<init>()V\n"
                + "    return\n"
                + ".end method\n\n";
        Node mainNode = n.getChildren().getFirst();
        return header + constructor + mainNode.accept(this);
    }

    @Override
    public String visitMain(MainNode n) {
        String start = ".method public static main([Ljava/lang/String;)V\n"
                + "    .limit stack 10\n"
                + "    .limit locals 10\n";

        Node blockNode = n.getChildren().getFirst();
        String body = blockNode.accept(this);

        String end = ".end method\n";

        return start + body + end;
    }

    @Override
    public String visitFunc(FuncDeclNode n) {
        return "";
    }

    @Override
    public String visitBlock(BlockNode n) {
        StringBuilder sb = new StringBuilder();
        for (Node child : n.getChildren()) {
            sb.append(child.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitVarDecl(VarDeclNode n) {
        return "";
    }

    @Override
    public String visitIf(IfNode n) {
        return "";
    }

    @Override
    public String visitFor(ForNode n) {
        return "";
    }

    @Override
    public String visitReturn(ReturnNode n) {
        String expr = "";
        if (!n.getChildren().isEmpty()) {
            expr = n.getChildren().get(0).accept(this);
        }

        return expr + "    return\n";
    }

    @Override
    public String visitExprStmt(ExprStmtNode n) {
        String exprCode = n.getChildren().get(0).accept(this);
        return exprCode + "    pop\n";
    }

    @Override
    public String visitAssign(AssignNode n) {
        return "";
    }

    @Override
    public String visitCall(CallNode n) {
        return "";
    }

    @Override
    public String visitIdentifier(IdentifierNode n) {
        return "";
    }

    @Override
    public String visitIndex(IndexNode n) {
        return "";
    }

    @Override
    public String visitLiteral(LiteralNode n) {
        String type = n.getType();
        String val = n.value;
        return switch (type) {
            case "ASSOCIATE" -> {
                String content = val.substring(2, val.length() - 2);
                yield "    ldc \"" + content + "\"\n";
            }
            case "SLICE" -> "    ldc " + val + "\n";
            case "DOSSIER" -> {
                int boolVal = val.equals("alive") ? 1 : 0;
                yield "    iconst_" + boolVal + "\n";
            }
            case "MARK" -> {
                char c = val.charAt(1);
                yield "    ldc " + (int) c + "\n";
            }
            default -> "    ldc " + val + "\n";
        };
    }

    @Override
    public String visitUnary(UnaryOpNode n) {
        return "";
    }

    @Override
    public String visitBinary(BinOpNode n) {
        String left = n.getChildren().get(0).accept(this);
        String right = n.getChildren().get(1).accept(this);

        String type = n.getType();
        String prefix = "SLICE".equals(type) ? "f" : "i";

        String op = switch (n.op) {
            case "+" -> "add";
            case "-" -> "sub";
            case "*" -> "mul";
            case "/" -> "div";
            case "%" -> "rem";
            default  -> "add";
        };

        return left + right + "    " + prefix + op + "\n";
    }

    @Override
    public String visitArraySize(ArraySizeNode n) {
        return "";
    }

    @Override
    public String visitCast(CastNode n) {
        return "";
    }
}
