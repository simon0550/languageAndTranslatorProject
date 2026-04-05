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
      System.out.println("Succès !\n");
    } catch (Exception e) {
      System.err.println("Échec");
      e.printStackTrace();
    }
  }

  public void testSemanticTypeError() {
    System.out.println("--- Exécution du test TypeError ---");
    System.out.println("ATTENDU : Le programme doit afficher TypeError et s'arrêter");

    String code = "def main() {\n"
        + "    INT age = 25;\n"
        + "    age = \"vingt-cinq\"; # L'erreur est ici\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyseTree(ast);

    System.err.println("ÉCHEC : Le compilateur a laissé passer une erreur de type !");
  }

  public void testSemanticScopeError() {
    System.out.println("--- Exécution du test ScopeError ---");
    System.out.println("ATTENDU : Le programme doit afficher ScopeError et s'arrêter.");

    String code = "def main() {\n"
        + "    INT x = 10;\n"
        + "    FLOAT x = 3.14; # L'erreur est ici, x existe déjà\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyseTree(ast);

    System.err.println("ÉCHEC : Le compilateur a autorisé une double déclaration !");
  }

  public void testSemanticOperatorError() {
    System.out.println("--- Exécution du test OperatorError ---");
    System.out.println("ATTENDU : Le programme doit afficher OperatorError et s'arrêter.");

    String code = "def main() {\n"
        + "    INT calcul = 5 + \"pommes\"; # L'erreur est ici\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyseTree(ast);

    System.err.println("ÉCHEC : Le compilateur a autorisé un calcul invalide !");
  }

  public void testSemanticFinalReassignment() {
    System.out.println("--- Exécution du test Final Reassignment ---");
    System.out.println("ATTENDU : Le programme doit afficher ScopeError");

    String code = "def main() {\n"
        + "    final INT max = 100;\n"
        + "    max = 200;\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyseTree(ast);

    System.err.println("ÉCHEC");
  }
}