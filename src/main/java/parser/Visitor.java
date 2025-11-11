package parser;

public interface Visitor<R> {
    R visitProgram(ProgramNode n);
    R visitMain(MainNode n);
    R visitFunc(FuncDeclNode n);
    R visitBlock(BlockNode n);
    R visitVarDecl(VarDeclNode n);
    R visitIf(IfNode n);
    R visitFor(ForNode n);
    R visitReturn(ReturnNode n);
    R visitExprStmt(ExprStmtNode n);
    R visitAssign(AssignNode n);
    R visitCall(CallNode n);
    R visitIdentifier(IdentifierNode n);
    R visitIndex(IndexNode n);
    R visitLiteral(LiteralNode n);
    R visitUnary(UnaryOpNode n);
    R visitBinary(BinOpNode n);
    R visitArraySize(ArraySizeNode n);
}
