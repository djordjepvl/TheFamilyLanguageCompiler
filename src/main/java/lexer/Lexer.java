package lexer;

import lexer.token.Token;
import lexer.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final ScannerCore sc;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
            Map.entry("cash", TokenType.CASH),
            Map.entry("slice", TokenType.SLICE),
            Map.entry("dossier", TokenType.DOSSIER),
            Map.entry("crew", TokenType.CREW),
            Map.entry("associate", TokenType.ASSOCIATE),
            Map.entry("mark", TokenType.MARK),

            Map.entry("theSitDown", TokenType.THESITDOWN),
            Map.entry("sleepWithTheFishes", TokenType.SLEEPWITHTHEFISHES),
            Map.entry("organizeHit", TokenType.ORGANIZEHIT),
            Map.entry("intel", TokenType.INTEL),
            Map.entry("sendWord", TokenType.SENDWORD),
            Map.entry("forgetAboutIt", TokenType.FORGETABOUTIT),
            Map.entry("check", TokenType.CHECK),
            Map.entry("plan", TokenType.PLAN),
            Map.entry("runTheBooks", TokenType.RUNTHEBOOKS),
            Map.entry("snitch", TokenType.SNITCH),
            Map.entry("spill", TokenType.SPILL),

            Map.entry("alive", TokenType.ALIVE),
            Map.entry("whacked", TokenType.WHACKED),

            Map.entry("countin", TokenType.COUNTIN)
    );

    public Lexer(String source) {
        this.source = source;
        this.sc = new ScannerCore(source);
    }

    public List<Token> scanTokens() {
        while (!sc.isAtEnd()) {
            sc.beginToken();
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "\0", null, sc.getLine(), sc.getCol(), sc.getCol()));
        return tokens;
    }

    private void scanToken() {
        char c = sc.advance();

        switch (c) {
            case '(' -> add(TokenType.LPAREN);
            case ')' -> add(TokenType.RPAREN);
            case '[' -> add(TokenType.LBRACKET);
            case ']' -> add(TokenType.RBRACKET);
            case '{' -> add(TokenType.LBRACE);
            case '}' -> add(TokenType.RBRACE);
            case ',' -> add(TokenType.COMMA);
            case ';' -> add(TokenType.SEMICOLON);
            case ':' -> add(TokenType.COLON);

            case '+' -> add(TokenType.ADD);
            case '-' -> add(TokenType.SUBTRACT);
            case '*' -> add(TokenType.MULTIPLY);

            case '/' -> {
                if (sc.match('/')) {
                    while (!sc.isAtEnd() && sc.peek() != '\n') sc.advance();
                } else {
                    add(TokenType.DIVIDE);
                }
            }

            case '%' -> add(TokenType.MODULO);

            case '<' -> add(sc.match('=') ? TokenType.LE : TokenType.LT);
            case '>' -> add(sc.match('=') ? TokenType.GE : TokenType.GT);

            case '=' -> add(sc.match('=') ? TokenType.EQ : TokenType.ASSIGN);

            case '!' -> {
                if (sc.match('=')) add(TokenType.NEQ);
                else add(TokenType.NOT);
            }

            case '&' -> {
                if (sc.match('&')) add(TokenType.AND);
                else throw error("Expected '&' after '&' (use '&&' for AND)");
            }

            case '|' -> {
                if (sc.match('|')) add(TokenType.OR);
                else throw error("Expected '|' after '|' (use '||' for OR)");
            }

            case '$' -> {
                if (sc.match('$')) {
                    stringDollarDollar();
                } else {
                    throw error("Single '$' is not allowed. Use $$...$$ for strings.");
                }
            }

            case '\'' -> charLiteral();

            case '\n' -> tokens.add(new Token(
                    TokenType.NEWLINE, "\n", null,
                    sc.getStartLine(), sc.getStartCol(), sc.getStartCol()
            ));

            case ' ', '\r', '\t' -> {}

            default -> {
                if (Character.isDigit(c)) {
                    numberOrFloat();
                } else if (isIdentStart(c)) {
                    identifierOrKeyword();
                } else {
                    throw error("Unexpected character");
                }
            }
        }
    }

    private void numberOrFloat() {
        int startIdx = sc.getStartIdx();

        while (Character.isDigit(sc.peek())) sc.advance();

        boolean isFloat = false;
        if (sc.peek() == '.' && Character.isDigit(sc.peekNext())) {
            isFloat = true;
            sc.advance();
            while (Character.isDigit(sc.peek())) sc.advance();
        }

        String text = source.substring(startIdx, sc.getCur());

        char next = sc.peek();
        if (Character.isAlphabetic(next) || next == '_') {
            throw error("Character in numeric literal");
        }

        if (isFloat) addLiteralFloat(text);
        else addLiteralInt(text);
    }

    private void identifierOrKeyword() {
        int startIdx = sc.getStartIdx();
        while (isIdentPart(sc.peek())) sc.advance();
        String text = source.substring(startIdx, sc.getCur());

        TokenType type;

        if (text.length() == 5 &&
                text.startsWith("plan") &&
                text.charAt(4) >= 'B' &&
                text.charAt(4) <= 'Z') {

            type = TokenType.PLAN;

        } else {
            type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
        }

        add(type, text);
    }

    private void stringDollarDollar() {
        int startLine = sc.getStartLine();
        int startCol  = sc.getStartCol();

        StringBuilder sb = new StringBuilder();
        while (!sc.isAtEnd()) {
            char ch = sc.advance();
            if (ch == '$' && sc.match('$')) {
                String content = sb.toString();
                String wrapped = "$$" + content + "$$";

                tokens.add(new Token(
                        TokenType.STRING_LITERAL,
                        wrapped,
                        null,
                        startLine,
                        startCol,
                        sc.getCol() - 1
                ));
                return;
            }
            sb.append(ch);
        }
        throw error("Unterminated string literal (expected $$)");
    }

    private void charLiteral() {
        int startLine = sc.getStartLine();
        int startCol  = sc.getStartCol();

        if (sc.isAtEnd()) throw error("Unterminated char literal");
        char ch = sc.advance();

        if (ch == '\\') {
            if (sc.isAtEnd()) throw error("Unterminated char escape");
            ch = sc.advance();
        }

        if (!sc.match('\'')) throw error("Char literal must be a single character");

        String lex = "'" + ch + "'";
        tokens.add(new Token(
                TokenType.CHAR_LITERAL,
                lex,
                null,
                startLine,
                startCol,
                sc.getCol() - 1
        ));
    }

    private boolean isIdentStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isIdentPart(char c) {
        return isIdentStart(c) || Character.isDigit(c);
    }

    private void add(TokenType type) {
        String lex = source.substring(sc.getStartIdx(), sc.getCur());
        tokens.add(new Token(
                type,
                lex,
                null,
                sc.getStartLine(),
                sc.getStartCol(),
                sc.getCol() - 1
        ));
    }

    private void add(TokenType type, String text) {
        tokens.add(new Token(
                type,
                text,
                null,
                sc.getStartLine(),
                sc.getStartCol(),
                sc.getCol() - 1
        ));
    }

    private void addLiteralInt(String literal) {
        tokens.add(new Token(
                TokenType.INT_LITERAL,
                literal,
                Integer.valueOf(literal),
                sc.getStartLine(),
                sc.getStartCol(),
                sc.getCol() - 1
        ));
    }

    private void addLiteralFloat(String literal) {
        tokens.add(new Token(
                TokenType.FLOAT_LITERAL,
                literal,
                null,
                sc.getStartLine(),
                sc.getStartCol(),
                sc.getCol() - 1
        ));
    }

    private RuntimeException error(String msg) {
        String near = source.substring(
                sc.getStartIdx(),
                Math.min(sc.getCur(), source.length())
        );
        return new RuntimeException(
                "LEXER > " + msg + " at " +
                        sc.getStartLine() + ":" + sc.getStartCol() +
                        " near '" + near + "'"
        );
    }
}
