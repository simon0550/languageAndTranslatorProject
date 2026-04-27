package compiler.CodeGenerator;

import compiler.Lexer.Lexer;
import compiler.Parser.Node;
import compiler.Parser.Parser;
import compiler.SemanticAnalyzer.SemanticAnalyzer;
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
      byte[] bytecode = generator.generate(ast, "Main");

      try (FileOutputStream fos = new FileOutputStream("Main.class")) {
        fos.write(bytecode);
      }

      assertTrue("Compilation et génération réussies", true);
      System.out.println("Fichier Main.class généré avec succès.");

    } catch (Exception e) {
      fail("Erreur pendant la compilation : " + e.getMessage());
    }
  }
}