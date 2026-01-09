package intermediate;

import parser.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JasminVisitor implements Visitor<String> {
    private final Map<String, Integer> varMap = new HashMap<>();
    private int nextIndex = 1;
    private int labelCount = 0;
    private String className;
    public JasminVisitor(String className) {
        this.className = className;
    }

    private String getInstrPrefix(String type) {
        return switch (type) {
            case "SLICE"     -> "f";
            case "ASSOCIATE" -> "a";
            default          -> "i";
        };
    }

    @Override
    public String visitProgram(ProgramNode n) {
        String header = ".class public " + this.className + "\n"
                + ".super java/lang/Object\n"
                + ".field private static scanner Ljava/util/Scanner;\n\n";

        String constructor = ".method public <init>()V\n"
                + "    aload_0\n"
                + "    invokespecial java/lang/Object/<init>()V\n"
                + "    return\n"
                + ".end method\n\n";

        StringBuilder sb = new StringBuilder();
        sb.append(header).append(constructor);

        for (Node child : n.getChildren()) {
            sb.append(child.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visitMain(MainNode n) {
        String start = ".method public static main([Ljava/lang/String;)V\n"
                + "    .limit stack 50\n"
                + "    .limit locals 50\n"
                + "    new java/util/Scanner\n"
                + "    dup\n"
                + "    getstatic java/lang/System/in Ljava/io/InputStream;\n"
                + "    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V\n"
                + "    putstatic " + this.className + "/scanner Ljava/util/Scanner;\n";

        Node blockNode = n.getChildren().getFirst();
        String body = blockNode.accept(this);

        // If the body doesn't end in a return, this ensures the .class is valid
        String safety = "    return\n";
        String end = ".end method\n";

        return start + body + safety + end;
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
        int index = nextIndex++;
        varMap.put(n.name, index);
        if (!n.getChildren().isEmpty()) {
            Node initExpr = n.getChildren().getFirst();
            String initCode = initExpr.accept(this);

            String prefix = getInstrPrefix(initExpr.getType());
            return initCode + "    " + prefix + "store " + index + "\n";
        }
        return "";
    }

    @Override
    public String visitIf(IfNode n) {
        String labelElse = "else_" + labelCount++;
        String labelEnd = "end_if_" + labelCount++;
        StringBuilder sb = new StringBuilder();
        sb.append(n.getChildren().getFirst().accept(this));
        sb.append("    ifeq ").append(labelElse).append("\n");
        sb.append(n.getChildren().get(1).accept(this));
        sb.append("    goto ").append(labelEnd).append("\n");
        sb.append(labelElse).append(":\n");
        if (n.getChildren().size() == 3) {
            sb.append(n.getChildren().get(2).accept(this));
        }
        sb.append(labelEnd).append(":\n");
        return sb.toString();
    }

    @Override
    public String visitFor(ForNode n) {
        return "";
    }

    @Override
    public String visitReturn(ReturnNode n) {
        String expr = "";
        if (!n.getChildren().isEmpty()) {
            expr = n.getChildren().getFirst().accept(this);
        }
        return expr + "    return\n";
    }

    @Override
    public String visitExprStmt(ExprStmtNode n) {
        String exprCode = n.getChildren().getFirst().accept(this);
        return exprCode + "    pop\n";
    }

    @Override
    public String visitAssign(AssignNode n) {
        // Usually: index 0 is the variable, index 1 is the value expression
        Node lhs = n.getChildren().get(0);
        Node rhs = n.getChildren().get(1);

        String valueCode = rhs.accept(this); // This will now correctly trigger visitCast

        IdentifierNode idNode = (IdentifierNode) lhs;
        int index = varMap.get(idNode.name);

        String prefix = getInstrPrefix(rhs.getType());
        return valueCode +
                "    dup\n" +
                "    " + prefix + "store " + index + "\n";
    }

    @Override
    public String visitCall(CallNode n) {
        if ("spill".equals(n.callee)) {
            StringBuilder sb = new StringBuilder();
            // Since it only accepts one variable, it's the first child
            Node argument = n.getChildren().getFirst();

            sb.append("    getstatic java/lang/System/out Ljava/io/PrintStream;\n");
            sb.append(argument.accept(this));

            String sig = switch (argument.getType()) {
                case "SLICE"     -> "(F)V";
                case "ASSOCIATE" -> "(Ljava/lang/String;)V";
                case "MARK"      -> "(C)V";
                case "DOSSIER"   -> "(Z)V";
                default          -> "(I)V"; // CASH
            };

            sb.append("    invokevirtual java/io/PrintStream/println").append(sig).append("\n");
            return sb.toString();
        }
        if ("snitch".equals(n.callee)) {
            StringBuilder sb = new StringBuilder();
            IdentifierNode idNode = (IdentifierNode) n.getChildren().getFirst();
            int index = varMap.get(idNode.name);
            String type = idNode.getType();

            sb.append("    getstatic ").append(this.className).append("/scanner Ljava/util/Scanner;\n");

            if ("MARK".equals(type)) {
                // 1. Get next token as String
                sb.append("    invokevirtual java/util/Scanner/next()Ljava/lang/String;\n");
                // 2. Get char at index 0
                sb.append("    iconst_0\n");
                sb.append("    invokevirtual java/lang/String/charAt(I)C\n");
                // 3. Store as an integer (chars are i-prefixed in JVM)
                sb.append("    istore ").append(index).append("\n");
            } else {
                String method = switch (type) {
                    case "SLICE"     -> "nextFloat()F";
                    case "ASSOCIATE" -> "next()Ljava/lang/String;";
                    case "DOSSIER"   -> "nextBoolean()Z";
                    default          -> "nextInt()I"; // CASH
                };
                String prefix = getInstrPrefix(type);
                sb.append("    invokevirtual java/util/Scanner/").append(method).append("\n");
                sb.append("    ").append(prefix).append("store ").append(index).append("\n");
            }
            return sb.toString();
        }
        return "";
    }

    @Override
    public String visitIdentifier(IdentifierNode n) {
        Integer index = varMap.get(n.name);
        String prefix = getInstrPrefix(n.getType());
        return "    " + prefix + "load " + index + "\n";
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
        List<String> relationalOps = Arrays.asList("==", "!=", "<", ">", "<=", ">=");

        if (relationalOps.contains(n.op)) {
            String instr = switch (n.op) {
                case "==" -> "if_icmpeq";
                case "!=" -> "if_icmpne";
                case "<"  -> "if_icmplt";
                case ">"  -> "if_icmpgt";
                case "<=" -> "if_icmple";
                case ">=" -> "if_icmpge";
                default   -> "";
            };

            String trueLabel = "cmp_true_" + labelCount++;
            String endLabel = "cmp_end_" + labelCount++;

            return left + right +
                    instr + " " + trueLabel + "\n" +
                    "iconst_0\n" +
                    "goto " + endLabel + "\n" +
                    trueLabel + ":\n" +
                    "iconst_1\n" +
                    endLabel + ":\n";
        }

        String type = n.getType();
        String prefix = "SLICE".equals(type) ? "f" : "i";
        String op = switch (n.op) {
            case "+" -> "add";
            case "-" -> "sub";
            case "*" -> "mul";
            case "/" -> "div";
            default  -> "rem";
        };

        return left + right + "    " + prefix + op + "\n";
    }

    @Override
    public String visitArraySize(ArraySizeNode n) {
        return "";
    }

    @Override
    public String visitCast(CastNode n) {
        String exprCode = n.getChildren().getFirst().accept(this);
        String sourceType = n.getChildren().getFirst().getType();
        String targetType = n.getType();

        if (sourceType.equals(targetType)) return exprCode;

        String castInstr = switch (sourceType + "2" + targetType) {
            case "CASH2SLICE" -> "i2f";
            case "SLICE2CASH" -> "f2i";
            case "MARK2CASH"  -> "";
            case "CASH2MARK"  -> "i2c";
            default           -> "";
        };

        return exprCode + "    " + castInstr + "\n";
    }
}
