package parser;

import lexer.token.Token;
import lexer.token.TokenType;
import parser.*;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    // javni ulaz: ovde ćemo pozvati program() kad dodamo pravila
    public ProgramNode parse() {
        try {
            return program(); // implementiramo u sledećem koraku
        } catch (ParseError e) {
            Token last = previous();
            Token at   = peek();
            String msg = "SYNTAX ERROR\n" +
                    "Last read: " + info(last) + "\n" +
                    "At token : " + info(at)   + "\n" +
                    "Message  : " + e.getMessage();
            throw new RuntimeException(msg, e);
        }
    }

    private String info(Token t) {
        return t.type + " '" + t.lexeme + "' at " + t.line + ":" + t.colStart;
    }

    private ProgramNode program() {
        ProgramNode prog = new ProgramNode();
        while (!isAtEnd()) {
            // EOF prekida petlju automatski jer check(EOF) postaje true
            if (check(TokenType.EOF)) break;
            prog.add(declOrStmt());
        }
        // očekujemo zaista EOF na kraju
        consume(TokenType.EOF, "Expected end of file");
        return prog;
    }

    // DeclOrStmt → (kasnije FuncDecl | VarDecl) | Stmt
// Ovde još uvek minimalno: prepoznaj blok ili fallback na naredbu
    private Node declOrStmt() {
        // Block na bilo kom nivou
        if (atBlockStart()) return block();

        // Deklaracija? (tip ili forgetAboutIt)
        if (isTypeToken(peek().type) || check(TokenType.FORGETABOUTIT)) {
            // lookahead: Type/forget + IDENT + (  → FuncDecl
            TokenType first = peek().type;
            advance(); // pojedi tip/forgetAboutIt
            Token name = consumeOneOfIdent("Expected function/variable name after type");
            if (match(TokenType.LPAREN)) {
                // func: već smo pojeli LPAREN — delegiramo na helper koji nastavlja od već pojedenog
                return finishFuncDecl(first, name);
            } else {
                // var: delegiramo na helper koji završava var deklaraciju
                return finishVarDecl(first, name);
            }
        }

        // Inače: naredba (if/return/expr;)
        return stmt();
    }

    private Node finishVarDecl(TokenType firstTypeTok, Token nameTok) {
        String ty = typeNameFrom(firstTypeTok);
        VarDeclNode vd = new VarDeclNode(ty, nameTok.lexeme);
        if (match(TokenType.ASSIGN)) {
            vd.add(expression());
        }
        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
        return vd;
    }

    // FuncDecl → (Type | FORGETABOUTIT) IDENT '(' [ParamList] ')' Block
    private Node finishFuncDecl(TokenType firstTypeTok, Token nameTok) {
        String ret = typeNameFrom(firstTypeTok);
        FuncDeclNode fn = new FuncDeclNode(ret, nameTok.lexeme);

        // ParamList?
        if (!check(TokenType.RPAREN)) {
            fn.add(param());
            while (match(TokenType.COMMA)) fn.add(param());
        }
        consume(TokenType.RPAREN, "Expected ')' after parameter list");
        // Telo:
        fn.add(block());
        return fn;
    }

    // Param → Type IDENT
    private Node param() {
        Token t = consumeOneOfType("Expected parameter type");
        Token id = consumeOneOfIdent("Expected parameter name");
        return new VarDeclNode(t.lexeme, id.lexeme);
    }

    // helper: pojedi jedan od tvojih tipova i vrati token tipa (sa lexeme)
    private Token consumeOneOfType(String msg) {
        if (isTypeToken(peek().type)) return advance();
        throw error(msg);
    }

    // Block → LBRACE { DeclOrStmt } RBRACE
    private BlockNode block() {
        consume(TokenType.LBRACE, "Expected '{' to start a block");
        return finishBlock();
    }
    private BlockNode finishBlock() {
        BlockNode b = new BlockNode();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            b.add(declOrStmt());
        }
        consume(TokenType.RBRACE, "Expected '}' to close a block");
        return b;
    }

    // ============ token helpers ============
    private boolean match(TokenType... types) {
        for (TokenType t : types) if (check(t)) { advance(); return true; }
        return false;
    }
    private boolean check(TokenType t) { return !isAtEnd() && peek().type == t; }
    private Token advance() { if (!isAtEnd()) current++; return previous(); }
    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private Node stmt() {
        if (atBlockStart()) return block();                  // Block
        if (match(TokenType.INTEL)) return ifStmt();         // IfStmt
        if (match(TokenType.SLEEPWITHTHEFISHES, TokenType.SENDWORD)) {
            return returnStmt(previous());                   // ReturnStmt
        }
        // ExprStmt
        Node e = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression");
        ExprStmtNode st = new ExprStmtNode();
        st.add(e);
        return st;
    }

    // IfStmt → INTEL '(' Expression ')' Block [ PLANB Block ]
    private Node ifStmt() {
        consume(TokenType.LPAREN, "Expected '(' after intel");
        Node cond = expression();
        consume(TokenType.RPAREN, "Expected ')' after condition");
        IfNode n = new IfNode();
        n.add(cond);
        n.add(block());            // then-block
        if (match(TokenType.PLANB)) n.add(block()); // else-block (opciono)
        return n;
    }

    // ReturnStmt → (SLEEPWITHTHEFISHES | SENDWORD) [Expression] SEMICOLON
    private Node returnStmt(Token kw) {
        ReturnNode r = new ReturnNode();
        if (!check(TokenType.SEMICOLON)) r.add(expression());
        consume(TokenType.SEMICOLON, "Expected ';' after return");
        return r;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(message + " (expected " + type + ")");
    }

    private static class ParseError extends RuntimeException { public ParseError(String m){ super(m); } }
    private ParseError error(String message) { return new ParseError(message + " [saw " + peek().type + "]"); }

    // Pogledaj sledeći token (lookahead +1)
    private Token peekNext() {
        if (current + 1 >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(current + 1);
    }

    // Da li sledeći token otvara blok?
    private boolean atBlockStart() {
        return check(TokenType.LBRACE);
    }

    // Da li je token neki od tvojih tipova
    private boolean isTypeToken(TokenType t) {
        return t == TokenType.CASH
                || t == TokenType.SLICE
                || t == TokenType.DOSSIER
                || t == TokenType.CREW
                || t == TokenType.ASSOCIATE
                || t == TokenType.MARK;
    }

    private Token consumeOneOfIdent(String msg) {
        if (check(TokenType.IDENTIFIER)) return advance();
        if (check(TokenType.THESITDOWN)) return advance(); // dozvoli "theSitDown" kao ime
        throw error(msg);
    }

    // Pretvori TokenType tipa u string tipa (lexeme), ili "forgetAboutIt" za void
    private String typeNameFrom(TokenType t) {
        if (t == TokenType.FORGETABOUTIT) return "forgetAboutIt";
        return peekPreviousLexemeOfType(t);
    }

    // pošto smo već pojeli tip token gore, njegov lexeme je u previous()
    private String peekPreviousLexemeOfType(TokenType t) {
        return previous().lexeme; // lexeme od tipa (cash/slice/...)
    }

    private Node expression() { return logicOr(); }

    private Node logicOr() {
        Node left = logicAnd();
        while (match(TokenType.OR)) {
            BinOpNode n = new BinOpNode("||");
            n.add(left); n.add(logicAnd());
            left = n;
        }
        return left;
    }

    private Node logicAnd() {
        Node left = equality();
        while (match(TokenType.AND)) {
            BinOpNode n = new BinOpNode("&&");
            n.add(left); n.add(equality());
            left = n;
        }
        return left;
    }

    private Node equality() {
        Node left = rel();
        while (match(TokenType.EQ, TokenType.NEQ)) {
            String op = previous().type == TokenType.EQ ? "==" : "!=";
            BinOpNode n = new BinOpNode(op);
            n.add(left); n.add(rel());
            left = n;
        }
        return left;
    }

    private Node rel() {
        Node left = add();
        while (match(TokenType.LT, TokenType.LE, TokenType.GT, TokenType.GE)) {
            String op = previous().lexeme; // "<", "<=", ">", ">="
            BinOpNode n = new BinOpNode(op);
            n.add(left); n.add(add());
            left = n;
        }
        return left;
    }

    private Node add() {
        Node left = mul();
        while (match(TokenType.ADD, TokenType.SUBTRACT)) {
            String op = previous().type == TokenType.ADD ? "+" : "-";
            BinOpNode n = new BinOpNode(op);
            n.add(left); n.add(mul());
            left = n;
        }
        return left;
    }

    private Node mul() {
        Node left = unary();
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO)) {
            String op = previous().lexeme; // "*", "/", "%"
            BinOpNode n = new BinOpNode(op);
            n.add(left); n.add(unary());
            left = n;
        }
        return left;
    }

    private Node unary() {
        if (match(TokenType.NOT, TokenType.SUBTRACT)) {
            String op = previous().type == TokenType.NOT ? "!" : "-";
            UnaryOpNode u = new UnaryOpNode(op);
            u.add(unary());
            return u;
        }
        return primary();
    }

    private Node primary() {
        if (match(TokenType.INT_LITERAL, TokenType.FLOAT_LITERAL,
                TokenType.STRING_LITERAL, TokenType.CHAR_LITERAL,
                TokenType.ALIVE, TokenType.WHACKED)) {
            return new LiteralNode(previous().lexeme);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new IdentifierNode(previous().lexeme);
        }
        if (match(TokenType.LPAREN)) {
            Node e = expression();
            consume(TokenType.RPAREN, "Expected ')'");
            return e;
        }
        throw error("Expected expression");
    }
}

