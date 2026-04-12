package compiler.SemanticAnalyzer;

import compiler.Lexer.Lexer;
import compiler.Parser.Node;
import compiler.Parser.Parser;

import java.io.Reader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Test;

public class TestSemanticAnalyzer extends TestCase {

  public void testSemanticValide() {
    System.out.println("--- Exécution du test valide ---");

    String code = "# Good luck\n" +
            "\n" +
            "final INT i = 3;\n" +
            "final FLOAT j = 3.2*5.0;\n" +
            "final INT k = i*3;\n" +
            "final STRING message = \"Hello\";\n" +
            "final BOOL isEmpty  = true;\n" +
            "\n" +
            "coll Point {\n" +
            "    INT x;\n" +
            "    INT y;\n" +
            "}\n" +
            "coll Person {\n" +
            "    STRING name;\n" +
            "    Point location;\n" +
            "    INT[] history;\n" +
            "}\n" +
            "\n" +
            "INT a = 3;\n" +
            "INT[] c  = INT ARRAY [5] ;  # new ARRAY of length 5\n" +
            "Person d = Person(\"me\", Point(3,7), INT ARRAY [i*2] ); # new person\n" +
            "\n" +
            "def INT square(INT v) {\n" +
            "    return v*v;\n" +
            "}\n" +
            "\n" +
            "def Point copyPoints(Point[] p) {\n" +
            "    return Point(p[0].x+p[1].x, p[0].y+p[1].y);\n" +
            "}\n" +
            "\n" +
            "def main() {\n" +
            "    INT value = read_INT();\n" +
            "    println(square(value));\n" +
            "    INT i;\n" +
            "    for (i; 1 -> 100; i+1) {\n" +
            "        while (value=/=3) {\n" +
            "            if (i > 10){\n" +
            "                value = value - 1;\n" +
            "            } else {\n" +
            "                println(message);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    i = (i+2)*2;\n" +
            "}";

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
    System.out.println("ATTENDU : Le programme doit afficher OperatorError et s'arrêter");

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

  public void testSemanticMissingConditionError() {
    System.out.println("--- Exécution du test MissingConditionError ---");
    System.out.println("ATTENDU : Le programme doit afficher MissingConditionError");

    String code = "def main() {\n"
        + "    INT x = 10;\n"
        + "    if (x) {\n"
        + "        x = 5;\n"
        + "    }\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyseTree(ast);

    System.err.println("ÉCHEC");
  }

  public void testSemanticUndeclaredVariable() {
    System.out.println("--- Exécution du test Variable non déclarée ---");
    System.out.println("ATTENDU : Le programme doit afficher ScopeError");

    String code = "def main() {\n"
        + "    INT x = y + 5;\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyseTree(ast);

    System.err.println("ÉCHEC");
  }

  public void testFinalError(){
    String code = "# Good luck\n"
        + "\n"
        + "final INT i = 3;\n"
        + "final FLOAT j = 3.2*5.0;\n"
        + "final INT k = i*3;\n"
        + "final STRING message = \"Hello\";\n"
        + "final BOOL isEmpty  = true;\n"
        + "\n"
        + "coll Point {\n"
        + "    INT x;\n"
        + "    INT y;\n"
        + "}\n"
        + "coll Person {\n"
        + "    STRING name;\n"
        + "    Point location;\n"
        + "    INT[] history;\n"
        + "}\n"
        + "\n"
        + "INT a = 3;\n"
        + "INT[] c  = INT ARRAY [5] ;  # new ARRAY of length 5\n"
        + "Person d = Person(\"me\", Point(3,7), INT ARRAY [i*2] ); # new person\n"
        + "\n"
        + "def INT square(INT v) {\n"
        + "    return v*v;\n"
        + "}\n"
        + "\n"
        + "def Point copyPoints(Point[] p) {\n"
        + "    return Point(p[0].x+p[1].x, p[0].y+p[1].y);\n"
        + "}\n"
        + "\n"
        + "def main() {\n"
        + "    INT value = read_INT();\n"
        + "    println(square(value));\n"
        + "    INT i;\n"
        + "    for (i; 1 -> 100; i+1) {\n"
        + "        while (value=/=3) {\n"
        + "            if (i > 10){\n"
        + "                value = value - 1;\n"
        + "            } else {\n"
        + "                println(message);\n"
        + "            }\n"
        + "        }\n"
        + "    }\n"
        + "\n"
        + "    i = (i+2)*2;\n"
        + "}";

    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
    semanticAnalyzer.analyseTree(ast);
  }
  @Test
  public void testFinalCodeValid() {
    String code = "# Good luck\n" +
            "final INT i = 3;\n" +
            "final FLOAT j = 3.2*5.0;\n" +
            "final INT k = i*3;\n" +
            "final STRING message = \"Hello\";\n" +
            "final BOOL isEmpty  = true;\n" +
            "\n" +
            "coll Point {\n" +
            "    INT x;\n" +
            "    INT y;\n" +
            "}\n" +
            "coll Person {\n" +
            "    STRING name;\n" +
            "    Point location;\n" +
            "    INT[] history;\n" +
            "}\n" +
            "\n" +
            "INT a = 3;\n" +
            "INT[] c  = INT ARRAY [5];\n" +
            "Person d = Person(\"me\", Point(3,7), INT ARRAY [i*2] );\n" +
            "\n" +
            "def INT square(INT v) {\n" +
            "    return v*v;\n" +
            "}\n" +
            "\n" +
            "def Point copyPoints(Point[] p) {\n" +
            "    return Point(p[0].x+p[1].x, p[0].y+p[1].y);\n" +
            "}\n" +
            "\n" +
            "def main() {\n" +
            "    INT value = read_INT();\n" +
            "    println(square(value));\n" +
            "    INT i;\n" +
            "    for (i; 1 -> 100; i+1) {\n" +
            "        while (value=/=3) {\n" +
            "            if (i > 10){\n" +
            "                value = value - 1;\n" +
            "            } else {\n" +
            "                println(message);\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    i = (i+2)*2;\n" +
            "}";

    try {
      Reader reader = new StringReader(code);
      Lexer lexer = new Lexer(reader);
      Parser parser = new Parser(lexer);
      Node ast = parser.getAST();

      SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
      semanticAnalyzer.analyseTree(ast);

      // Si on arrive jusqu'ici, c'est que ni le Parser ni le SemanticAnalyzer n'ont planté.
      assertTrue("L'analyse sémantique s'est terminée avec succès", true);

    } catch (Exception e) {
      // Si une exception est levée, on force l'échec du test
      fail("Le compilateur ne devrait pas crasher sur un code valide. Erreur : " + e.getMessage());
    }
  }
}