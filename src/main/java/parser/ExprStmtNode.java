package parser;

public class ExprStmtNode extends Node{
    @Override public <R> R accept(Visitor<R> v){ return v.visitExprStmt(this); }
}
