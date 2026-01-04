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
            System.exit(64);
        }
        String code = Files.readString(Path.of(args[0]));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = null;
        try {
            tokens = lexer.scanTokens();
        } catch (Exception lexErr) {
            System.err.println("[LEX ERROR] " + lexErr.getMessage());
            System.exit(65);
        }
        ProgramNode ast;
        try {
            Parser parser = new Parser(tokens);
            ast = parser.parse();
        } catch (RuntimeException parseErr) {
            System.err.println(parseErr.getMessage());
            System.err.println("\n-- TOKENS --");
            for (Token t : tokens) System.err.println(TokenFormatter.format(t));
            System.exit(65);
            return;
        }

        Path outputDir = Path.of("output");
        if (!Files.exists(outputDir)) Files.createDirectories(outputDir);

        // 2. Define the base name of your input file
        Path inputPath = Path.of(args[0]);
        // 2. Get filename without .don extension (e.g., "primer5")
        String baseName = inputPath.getFileName().toString().replaceFirst("\\.[^.]+$", "");

        // 3. Save the AST and JSON for debugging
        Files.writeString(outputDir.resolve(baseName + "_ast.txt"), ast.printTree());
        Files.writeString(outputDir.resolve(baseName + "_ast.json"), ast.toJson());

        try {
            String pretty = ast.accept(new AstPrinter());
            System.out.println("== AST (Visitor / AstPrinter) ==");
            System.out.println(pretty);
        } catch (Throwable ignore) {

        }
    }
}