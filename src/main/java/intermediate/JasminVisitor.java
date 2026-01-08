package intermediate;

import parser.*;

public class JasminVisitor implements Visitor<String> {

    private String className;
    public JasminVisitor(String className) {
        this.className = className;
    }

    @Override
    public String visitProgram(ProgramNode n) {
        return "test";
    }

    @Override
    public String visitMain(MainNode n) {
        return "";
    }

    @Override
    public String visitFunc(FuncDeclNode n) {
        return "";
    }

    @Override
    public String visitBlock(BlockNode n) {
        return "";
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
