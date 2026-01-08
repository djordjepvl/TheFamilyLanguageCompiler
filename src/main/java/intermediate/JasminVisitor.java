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

        String end = "    return\n"
                + ".end method\n";

        return start + body + end;
    }

    @Override
    public String visitFunc(FuncDeclNode n) {
        return "";
    }

    @Override
    public String visitBlock(BlockNode n) {
        return "    test\n";
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
        return "";
    }

    @Override
    public String visitExprStmt(ExprStmtNode n) {
        return "";
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
        return "";
    }

    @Override
    public String visitUnary(UnaryOpNode n) {
        return "";
    }

    @Override
    public String visitBinary(BinOpNode n) {
        return "";
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
