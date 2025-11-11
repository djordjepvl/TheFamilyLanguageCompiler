package parser;

import parser.Node;

public class ProgramNode extends Node {
    @Override public <R> R accept(Visitor<R> v) { return v.visitProgram(this); }
}
