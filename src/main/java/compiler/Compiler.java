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
        String outputPath = "test.class";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o") && i + 1 < args.length) {
                outputPath = args[i + 1];
                break;
            }
        }
        File outFile = new File(outputPath);
        String parentDir = outFile.getParent();
        String dirPrefix = (parentDir == null) ? "" : parentDir + File.separator;
        String inputPath = args[0];

        try {
            Lexer lexer = new Lexer(new FileReader(inputPath));
            Parser parser = new Parser(lexer);
            Node ast = parser.getAST();

            SemanticAnalyzer s = new SemanticAnalyzer();
            s.analyseTree(ast);

            String className = outFile.getName().replace(".class", "");

            CodeGenerator cg = new CodeGenerator();
            Map<String, byte[]> classes = cg.generate(ast, className);
            for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
                String fileName = dirPrefix + entry.getKey() + ".class";
                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    fos.write(entry.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}