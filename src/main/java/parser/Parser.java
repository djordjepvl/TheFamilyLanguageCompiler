package parser;

import lexer.token.Token;
import lexer.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    // ==== PUBLIC ENTRY =======================================================

    public ProgramNode parse() {
        try {
            skipNewlines();
            ProgramNode p = new ProgramNode();

            // 1) obavezni main
            p.add(parseMainDef());

            // 2) nula-više funkcija
            skipNewlines();
            while (!check(TokenType.EOF)) {
                skipNewlines();
                if (check(TokenType.EOF)) break;
                p.add(parseFuncDef());
                skipNewlines();
            }

            consume(TokenType.EOF, "Expected end of file");
            return p;
        } catch (ParseError e) {
            Token last = (current > 0) ? previous() : peek();
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

    // ==== MAIN ===============================================================

    // theSitDown() { block }
    private Node parseMainDef() {
        consume(TokenType.THESITDOWN, "Expected 'theSitDown'");
        consume(TokenType.LPAREN, "Expected '(' after theSitDown");
        consume(TokenType.RPAREN, "Expected ')' after theSitDown()");
        skipNewlines();
        BlockNode body = block(); // block() očekuje { ... }
        // (semantika posle): da u telu postoji sleepWithTheFishes;
        return new MainNode(body);
    }

    // ==== FUNKCIJE ===========================================================

    // organizeHit IDENT '(' [params] ')' [intel type] '{' block '}'
    private Node parseFuncDef() {
        consume(TokenType.ORGANIZEHIT, "Expected 'organizeHit'");
        Token name = consume(TokenType.IDENTIFIER, "Expected function name");

        consume(TokenType.LPAREN, "Expected '(' after function name");
        List<Node> params = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            params.add(param());
            while (match(TokenType.COMMA)) { skipNewlines(); params.add(param()); }
        }
        consume(TokenType.RPAREN, "Expected ')' after parameters");

        String retKind = "forgetAboutIt"; // default: void
        if (match(TokenType.INTEL)) {
            retKind = parseTypeName(); // podržava i crew[ type ]
        }

        skipNewlines();
        BlockNode body = block();

        FuncDeclNode f = new FuncDeclNode(retKind, name.lexeme);
        for (Node prm : params) f.add(prm);
        f.add(body);
        return f;
    }

    // Param – koristimo "Type IDENT" (u skladu s tvojim dosadašnjim kodom)
    private Node param() {
        String tname = parseTypeName();
        Token id = consume(TokenType.IDENTIFIER, "Expected parameter name");
        return new VarDeclNode(tname, id.lexeme);
    }

    // ==== BLOK / DEKL / STATEMENT ===========================================

    // Block → '{' { DeclOrStmt } '}'
    private BlockNode block() {
        consume(TokenType.LBRACE, "Expected '{' to start a block");
        BlockNode b = new BlockNode();
        skipNewlines();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            b.add(declOrStmt());
            skipNewlines();
        }
        consume(TokenType.RBRACE, "Expected '}' to close a block");
        return b;
    }

    // DeclOrStmt → VarDecl ';' | Stmt
    private Node declOrStmt() {
        skipNewlines();
        if (atBlockStart()) return block();

        if (isTypeStart()) {
            Node vd = varDecl();
            consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");
            return vd;
        }

        return stmt();
    }

    // VarDecl: type decl_list
    // decl_list: IDENT [ '=' expr ] { ',' IDENT [ '=' expr ] }
    private Node varDecl() {
        String tname = parseTypeName();

        // prvi deklarator
        Token id = consume(TokenType.IDENTIFIER, "Expected variable name");
        VarDeclNode first = new VarDeclNode(tname, id.lexeme);

        // --- NOVO: opciono 'countin <expr>' po deklaratoru ---
        if (match(TokenType.COUNTIN)) {
            Node sizeExpr = expression();
            // konvencija: dete #0 = init, dete #1 = size (ili obrnuto, ali dosledno)
            // ovde stavljamo size kao posebno dete radi semantike
            // (možeš napraviti i ArrayDeclNode ako želiš eksplicitnije)
            first.add(new ArraySizeNode(sizeExpr)); // ili samo add(sizeExpr) i razlikuj po tipu čvora
        }

        if (match(TokenType.ASSIGN)) {
            first.add(expression());
        }

        VarDeclNode root = first;

        // ostali deklaratori: , name [countin expr] [= expr]
        while (match(TokenType.COMMA)) {
            Token id2 = consume(TokenType.IDENTIFIER, "Expected variable name after ','");
            VarDeclNode next = new VarDeclNode(tname, id2.lexeme);

            if (match(TokenType.COUNTIN)) {
                Node sizeExpr2 = expression();
                next.add(new ArraySizeNode(sizeExpr2)); // vidi napomenu gore
            }
            if (match(TokenType.ASSIGN)) {
                next.add(expression());
            }
            root.add(next);
        }
        return root;
    }

    private Node stmt() {
        skipNewlines();
        if (atBlockStart()) return block();

        // if
        if (match(TokenType.CHECK)) return ifStmt();

        // for
        if (match(TokenType.RUNTHEBOOKS)) return forStmt();

        if (match(TokenType.FORGETABOUTIT)) {
            ReturnNode r = new ReturnNode("forgetAboutIt");
            consume(TokenType.SEMICOLON, "Expected ';' after forgetAboutIt");
            return r;
        }

        // ugrađeni pozivi kao statement
        if (check(TokenType.SPILL) || check(TokenType.SNITCH)) {
            Node c = callStmt();
            consume(TokenType.SEMICOLON, "Expected ';' after call");
            return c;
        }

        // return/sleep/sendWord
        if (check(TokenType.SLEEPWITHTHEFISHES) || check(TokenType.SENDWORD)) {
            return returnStmt(advance());
        }

        // dodela ili izraz/poziv
        Node s = maybeAssignOrExprStmt();
        consume(TokenType.SEMICOLON, "Expected ';' after statement");
        return s;
    }

    // check (cond) { then } [ planB { else } ]
    private Node ifStmt() {
        // prvi if
        consume(TokenType.LPAREN, "Expected '(' after check");
        skipNewlines();
        Node cond = expression();
        skipNewlines();
        consume(TokenType.RPAREN, "Expected ')' after condition");
        skipNewlines();

        IfNode root = new IfNode();
        root.add(cond);
        root.add(block());

        // else-if lanci: svaka naredna grana počinje sa CHECK
        IfNode cursor = root;
        while (match(TokenType.CHECK)) {
            consume(TokenType.LPAREN, "Expected '(' after check");
            skipNewlines();
            Node cond2 = expression();
            skipNewlines();
            consume(TokenType.RPAREN, "Expected ')' after condition");
            skipNewlines();

            IfNode next = new IfNode();
            next.add(cond2);
            next.add(block());
            // zakači kao "else" na prethodni
            cursor.add(next);
            cursor = next;
        }

        // opcioni planB { ... } kao poslednji else
        if (match(TokenType.PLANB)) {
            skipNewlines();
            cursor.add(block());
        }
        return root;
    }

    // runTheBooks (init ; cond ; step) { body }
    private Node forStmt() {
        consume(TokenType.LPAREN, "Expected '(' after runTheBooks");

        Node init = null;
        if (!check(TokenType.SEMICOLON)) {
            if (isTypeStart()) init = varDecl();
            else init = maybeAssignOrExprStmtCore();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after for-init");

        Node cond = null;
        if (!check(TokenType.SEMICOLON)) cond = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after for-condition");

        Node step = null;
        if (!check(TokenType.RPAREN)) step = maybeAssignOrExprStmtCore();
        consume(TokenType.RPAREN, "Expected ')' after for-step");

        skipNewlines();
        BlockNode body = block();

        ForNode f = new ForNode();
        if (init != null) f.add(init);
        if (cond != null) f.add(cond);
        if (step != null) f.add(step);
        f.add(body);
        return f;
    }

    // built-ins kao statement
    private Node callStmt() {
        if (match(TokenType.SPILL)) {
            consume(TokenType.LPAREN, "Expected '(' after spill");
            Node e = expression();
            consume(TokenType.RPAREN, "Expected ')' after spill arg");
            CallNode c = new CallNode("spill"); c.add(e); return c;
        }
        if (match(TokenType.SNITCH)) {
            consume(TokenType.LPAREN, "Expected '(' after snitch");
            Node lv = parseLValue();
            consume(TokenType.RPAREN, "Expected ')' after snitch arg");
            CallNode c = new CallNode("snitch"); c.add(lv); return c;
        }
        throw error("Internal: callStmt dispatched on non-builtin");
    }

    // (sleepWithTheFishes | sendWord) [expr] ;
    private Node returnStmt(Token kw) {
        String kind = (kw.type == TokenType.SENDWORD) ? "sendWord" : "sleepWithTheFishes";
        ReturnNode r = new ReturnNode(kind);
        if (!check(TokenType.SEMICOLON)) r.add(expression());
        consume(TokenType.SEMICOLON, "Expected ';' after return");
        return r;
    }

    // ==== IZRAZI =============================================================

    private Node expression() { return assignment(); }

    // assignment: lvalue '=' assignment | logicOr
    private Node assignment() {
        int save = current;
        try {
            Node lv = parseLValue();
            if (match(TokenType.ASSIGN)) {
                Node val = assignment(); // desno-asocijativno
                AssignNode asg = new AssignNode(lv);
                asg.add(val);
                return asg;
            }
            // nije dodela — vrati se na početak i parsiraj kao logički izraz
            current = save;
        } catch (ParseError ignore) { current = save; }
        return logicOr();
    }

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
            String op = (previous().type == TokenType.EQ) ? "==" : "!=";
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
            String op = (previous().type == TokenType.ADD) ? "+" : "-";
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
            String op = (previous().type == TokenType.NOT) ? "!" : "-";
            UnaryOpNode u = new UnaryOpNode(op);
            u.add(unary());
            return u;
        }
        return primary();
    }

    private Node primary() {
        // literali
        if (match(TokenType.INT_LITERAL, TokenType.FLOAT_LITERAL,
                TokenType.STRING_LITERAL, TokenType.CHAR_LITERAL,
                TokenType.ALIVE, TokenType.WHACKED)) {
            return new LiteralNode(previous().lexeme);
        }

        // korisnički poziv ili lvalue/ident
        if (check(TokenType.IDENTIFIER)) {
            // pogledaj da li sledi '(' => poziv
            Token id = advance();
            String name = id.lexeme;

            if (match(TokenType.LPAREN)) {
                CallNode call = new CallNode(name);
                if (!check(TokenType.RPAREN)) {
                    call.add(expression());
                    while (match(TokenType.COMMA)) { skipNewlines(); call.add(expression()); }
                }
                consume(TokenType.RPAREN, "Expected ')' after arguments");
                return call;
            }

            // možda indeksiranje => lvalue u izrazu (npr. arr[i])
            Node base = new IdentifierNode(name);
            while (match(TokenType.LBRACKET)) {
                Node idx = expression();
                consume(TokenType.RBRACKET, "Expected ']' after index");
                base = new IndexNode(base, idx);
            }
            return base;
        }

        // (expr)
        if (match(TokenType.LPAREN)) {
            Node e = expression();
            consume(TokenType.RPAREN, "Expected ')'");
            return e;
        }

        // built-in bez '(' nije dozvoljen u izrazu
        if (check(TokenType.SPILL) || check(TokenType.SNITCH)) {
            throw error("Expected '(' after built-in name for a call");
        }

        throw error("Expected expression");
    }

    // lvalue: IDENT { '[' expr ']' }
    private Node parseLValue() {
        Token id = consume(TokenType.IDENTIFIER, "Expected identifier (lvalue)");
        Node base = new IdentifierNode(id.lexeme);
        while (match(TokenType.LBRACKET)) {
            Node idx = expression();
            consume(TokenType.RBRACKET, "Expected ']' after index");
            base = new IndexNode(base, idx);
        }
        return base;
    }

    // ==== TIPOVI =============================================================

    // type: base_type | CREW '[' type ']'
    // base_type: cash | slice | dossier | associate | mark
    private String parseTypeName() {
        if (match(TokenType.CREW)) {
            consume(TokenType.LBRACKET, "Expected '[' after crew");
            String inner = parseTypeName();
            consume(TokenType.RBRACKET, "Expected ']' after crew[...]");
            return "crew[" + inner + "]";
        }
        Token t = consumeOneOfType("Expected type name");
        return switch (t.type) {
            case CASH -> "cash";
            case SLICE -> "slice";
            case DOSSIER -> "dossier";
            case ASSOCIATE -> "associate";
            case MARK -> "mark";
            default -> t.lexeme.toLowerCase();
        };
    }

    private Node maybeAssignOrExprStmt() {
        Node n = assignment();          // može biti dodela ili običan izraz/poziv
        ExprStmtNode st = new ExprStmtNode();
        st.add(n);
        return st;
    }

    // Ista stvar, ali BEZ očekivanja ';' (koristi se u for-init i for-step)
    private Node maybeAssignOrExprStmtCore() {
        return assignment();            // samo vrati expression/assignment čvor
    }

    // ==== HELPERS ============================================================



    private boolean isTypeStart() {
        return check(TokenType.CREW) || isBaseType(peek().type);
    }
    private boolean isBaseType(TokenType t) {
        return t == TokenType.CASH
                || t == TokenType.SLICE
                || t == TokenType.DOSSIER
                || t == TokenType.ASSOCIATE
                || t == TokenType.MARK;
    }

    private Token consumeOneOfType(String msg) {
        if (isBaseType(peek().type)) return advance();
        throw error(msg);
    }

    private boolean atBlockStart() { return check(TokenType.LBRACE); }

    private void skipNewlines() { while (match(TokenType.NEWLINE)) { /* eat */ } }

    private boolean match(TokenType... types) {
        for (TokenType t : types) if (check(t)) { advance(); return true; }
        return false;
    }
    private boolean check(TokenType t) {
        if (peek().type == TokenType.EOF) return t == TokenType.EOF;
        return peek().type == t;
    }
    private Token advance() { if (!isAtEnd()) current++; return previous(); }
    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(message + " (expected " + type + ")");
    }

    private static class ParseError extends RuntimeException { public ParseError(String m){ super(m); } }
    private ParseError error(String message) { return new ParseError(message + " [saw " + peek().type + "]"); }
}
