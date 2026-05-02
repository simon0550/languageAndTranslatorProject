package compiler.CodeGenerator;

import compiler.Lexer.Lexer;
import compiler.Parser.Node;
import compiler.Parser.Parser;
import compiler.SemanticAnalyzer.SemanticAnalyzer;
import java.util.Map;
import junit.framework.TestCase;

import java.io.FileOutputStream;
import java.io.StringReader;

public class TestCodeGenerator extends TestCase {

  public void testSimpleCompilation() {
    String sourceCode = "def main() { \n" +
        "   int x = 10;\n" +
        "   println(x + 5);\n" +
        "}";

    try {
      Lexer lexer = new Lexer(new StringReader(sourceCode));
      Parser parser = new Parser(lexer);
      Node ast = parser.getAST();

      SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
      semanticAnalyzer.analyseTree(ast);
      System.out.println("Analyse sémantique réussie.");

      CodeGenerator generator = new CodeGenerator();
      Map<String, byte[]> classes = generator.generate(ast, "Main");

      for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
        String fileName = entry.getKey() + ".class";
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
          fos.write(entry.getValue());
          System.out.println("Fichier " + fileName + " généré avec succès.");
        }
      }

      assertTrue(true);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Erreur pendant la compilation : " + e.getMessage());
    }
  }
}