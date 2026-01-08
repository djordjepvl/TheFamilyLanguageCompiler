package application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import intermediate.JasminVisitor;
import lexer.Lexer;
import lexer.token.Token;
import lexer.token.TokenFormatter;
import parser.Parser;
import parser.ProgramNode;
import parser.AstPrinter;
import semantics.SemanticAnalyzer;
import semantics.SemanticError;

public class Application {
    public static void main(String[] args) throws Exception {
        System.out.println("ARGS = " + java.util.Arrays.toString(args));
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

        try {
            new SemanticAnalyzer().analyze(ast);
            System.out.println("SEMANTIC OK");
        } catch (SemanticError semErr) {
            System.err.println(semErr.pretty());
            System.err.println("\n-- TOKENS --");
            for (Token t : tokens) System.err.println(TokenFormatter.format(t));
            System.exit(66);
            return;
        } catch (RuntimeException semErr) {
            System.err.println("[SEMANTIC ERROR] " + semErr.getMessage());
            System.err.println("\n-- TOKENS --");
            for (Token t : tokens) System.err.println(TokenFormatter.format(t));
            System.exit(66);
            return;
        }

        Path outputDir = Path.of("output");
        if (!Files.exists(outputDir)) Files.createDirectories(outputDir);

        Path inputPath = Path.of(args[0]);
        String baseName = inputPath.getFileName().toString().replaceFirst("\\.[^.]+$", "");

        Files.writeString(outputDir.resolve(baseName + "_ast.txt"), ast.printTree());
        Files.writeString(outputDir.resolve(baseName + "_ast.json"), ast.toJson());

        JasminVisitor jasminVisitor = new JasminVisitor();
        ast.accept(jasminVisitor);
        String jasminCode = outputDir.resolve(baseName + ".j").toString();

        ProcessBuilder pbAssemble = new ProcessBuilder("java", "-jar", "lib/jasmin.jar", "-d", "output", jasminCode);
        pbAssemble.inheritIO();
        Process assemble = pbAssemble.start();
        int assembleResult = assemble.waitFor();
        System.out.println("Jasmin Exit Code: " + assembleResult);

        if (assembleResult == 0) {
            ProcessBuilder pbRun = new ProcessBuilder("java", "-cp", "output", baseName);
            pbRun.inheritIO();
            pbRun.start().waitFor();
        }
    }
}
