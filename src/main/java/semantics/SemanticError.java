package semantics;

import lexer.token.Token;

public class SemanticError extends RuntimeException {
    public final Token lastRead;
    public final Token atToken;

    public SemanticError(String message, Token lastRead, Token atToken) {
        super(message);
        this.lastRead = lastRead;
        this.atToken = atToken;
    }

    public String pretty() {
        return "SEMANTIC ERROR\n"
                + "Last read: " + info(lastRead) + "\n"
                + "At token : " + info(atToken) + "\n"
                + "Message  : " + getMessage();
    }

    private String info(Token t) {
        if (t == null) return "N/A";
        return t.type + " '" + t.lexeme + "' at " + t.line + ":" + t.colStart;
    }
}

