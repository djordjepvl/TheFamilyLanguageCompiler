package lexer.token;

public enum TokenType {
    // Tipovi podataka
    CASH,           // int
    SLICE,          // float
    DOSSIER,        // bool (alive/whacked)
    CREW,           // array
    ASSOCIATE,      // string
    MARK,           // char (ako postoji u vašim primerima)

    // Ključne reči (kontrola toka, funkcije, itd.)
    THESITDOWN,         // main funkcija
    SLEEPWITHTHEFISHES, // return/exit main
    ORGANIZEHIT,        // definicija funkcije
    INTEL,              // označava tip povratne vrednosti
    SENDWORD,           // return
    FORGETABOUTIT,      // void return
    CHECK,              // if
    PLAN,              // else
    RUNTHEBOOKS,        // for petlja
    SPILL,              // print
    SNITCH,             // input

    // Logičke konstante (dossier tip)
    ALIVE,              // true
    WHACKED,            // false

    // String delimiter
    STRING_DELIM,       // $$
    STRING_LITERAL,     // tekst između $$...$$

    // Literali
    INT_LITERAL,        // celobrojni literal (cash)
    FLOAT_LITERAL,      // decimalni literal (slice)
    CHAR_LITERAL,       // mark tip, npr. 'A'

    // Identifikatori
    IDENTIFIER,         // nazivi promenljivih i funkcija

    // Operator dodele
    ASSIGN,             // =

    // Aritmetički operatori
    ADD,                // +
    SUBTRACT,           // -
    MULTIPLY,           // *
    DIVIDE,             // /
    MODULO,             // %

    // Relacioni operatori
    LT,                 // <
    LE,                 // <=
    GT,                 // >
    GE,                 // >=
    EQ,                 // ==
    NEQ,                // !=

    // Logički operatori
    AND,                // &&
    OR,                 // ||
    NOT,                // !

    // Zagrade i separatori
    LPAREN,             // (
    RPAREN,             // )
    LBRACE,             // {
    RBRACE,             // }
    LBRACKET,           // [
    RBRACKET,           // ]
    COMMA,              // ,
    SEMICOLON,          // ;
    COLON,              // :

    // Ostalo
    NEWLINE,            // novi red (ako se tretira kao token)
    EOF,                // kraj fajla

    COUNTIN,

    // Greške
    UNKNOWN             // nepoznat token (za leksičku grešku)
}
