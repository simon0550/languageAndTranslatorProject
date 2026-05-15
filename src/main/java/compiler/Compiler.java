package compiler;

import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.Node;
import compiler.SemanticAnalyzer.SemanticAnalyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Map;

public class Compiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: gradle run --args=\"source.lang [-o output.class]\"");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = "Main.class";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o") && i + 1 < args.length) {
                outputPath = args[i + 1];
                break;
            }
        }

        try {
            Lexer lexer = new Lexer(new FileReader(inputPath));
            Parser parser = new Parser(lexer);
            Node ast = parser.getAST();

            SemanticAnalyzer s = new SemanticAnalyzer();
            s.analyseTree(ast);

            File outFile = new File(outputPath);
            String className = outFile.getName().replace(".class", "");

            File parentDir = outFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            CodeGenerator cg = new CodeGenerator();
            Map<String, byte[]> classes = cg.generate(ast, className);

            for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
                File classFile = new File(parentDir, entry.getKey() + ".class");
                try (FileOutputStream fos = new FileOutputStream(classFile)) {
                    fos.write(entry.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}