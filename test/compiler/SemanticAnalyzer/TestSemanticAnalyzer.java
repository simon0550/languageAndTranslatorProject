package compiler.SemanticAnalyzer;

import compiler.Lexer.Lexer;
import compiler.Parser.Node;
import compiler.Parser.Parser;
import compiler.SemanticAnalyser.SemanticAnalyzer;

import java.io.Reader;
import java.io.StringReader;
import junit.framework.TestCase;

public class TestSemanticAnalyzer extends TestCase {

  public void testSemanticValide() {
    System.out.println("--- Exécution du test valide ---");

    String code = "final INT max = 100;\n"
        + "INT score = 0;\n"
        + "\n"
        + "def main() {\n"
        + "    INT i = 1;\n"
        + "    while (i <= max) {\n"
        + "        if (i % 2 == 0) {\n"
        + "            score = score + i;\n"
        + "        }\n"
        + "        i = i + 1;\n"
        + "    }\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

    try {
      semanticAnalyzer.analyseTree(ast);
      System.out.println("Succès : Le code est sémantiquement correct !\n");
    } catch (Exception e) {
      System.err.println("Échec : Une exception inattendue a été levée.");
      e.printStackTrace();
    }
  }
}