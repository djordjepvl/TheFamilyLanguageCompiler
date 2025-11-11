package application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import lexer.Lexer;
import lexer.token.Token;
import lexer.token.TokenFormatter;
import parser.Parser;
import parser.ProgramNode;
import parser.AstPrinter;

public class Application {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java application.Application <source-file>");
            System.exit(64); // EX_USAGE
        }

        String code = Files.readString(Path.of(args[0]));

        // 1) LEXER
        Lexer lexer = new Lexer(code);
        List<Token> tokens = null;
        try {
            tokens = lexer.scanTokens();
        } catch (Exception lexErr) {
            System.err.println("[LEX ERROR] " + lexErr.getMessage());
            System.exit(65); // EX_DATAERR
        }

        // 2) PARSER
        ProgramNode ast;
        try {
            Parser parser = new Parser(tokens);
            ast = parser.parse();
        } catch (RuntimeException parseErr) {
            // lepši ispis greške + kratka tabela tokena
            System.err.println(parseErr.getMessage());
            System.err.println("\n-- TOKENS --");
            for (Token t : tokens) System.err.println(TokenFormatter.format(t));
            System.exit(65); // EX_DATAERR
            return;
        }

        // 3) ISPIS AST-a (outline + JSON)
        System.out.println("\n== AST (outline) ==");
        System.out.println(ast.printTree());

        System.out.println("== AST (JSON) ==");
        System.out.println(ast.toJson());

        // 4) (OPCIONO) Visitor ispis (S-expr/pretty)
        // Radi samo ako si dodao Visitor i AstPrinter.
        try {
            String pretty = ast.accept(new AstPrinter());
            System.out.println("== AST (Visitor / AstPrinter) ==");
            System.out.println(pretty);
        } catch (Throwable ignore) {
            // Ako još nemaš AstPrinter ili Visitor, preskoči tiho.
        }
    }
}