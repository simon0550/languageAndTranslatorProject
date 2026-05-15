package compiler.CodeGenerator;

import compiler.Lexer.Lexer;
import compiler.Parser.Node;
import compiler.Parser.Parser;
import compiler.SemanticAnalyzer.SemanticAnalyzer;

import java.io.FileReader;
import java.io.StringReader;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class TestCodeGenerator {
  Lexer lexer;
  Parser parser;
  Node ast;
  SemanticAnalyzer semanticAnalyzer;

  String sourceCode = "# Good luck\n" +
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

  @Before
  public void setUp() throws Exception {
    lexer = new Lexer(new FileReader("test.lang"));
    parser = new Parser(lexer);
    ast = parser.getAST();
    semanticAnalyzer = new SemanticAnalyzer();
  }

  @Test

  public void testSimpleCompilation() {
    try {

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


  @Test
  public void testBaseCompilation() {
    String text = """
            def main() {
                println("Test String");
                println(42);
                print("Hello ");
                print(100);
                println("");
                varInt = 99;
                println(varInt);
                println(3.14);
                println(true);
                println(5 + 5);
                println();
            }""";
    Lexer l = new Lexer(new StringReader(text));
    Parser p = new Parser(l);
    Node n = p.getAST();
    semanticAnalyzer.analyseTree(n);
    try {
      CodeGenerator generator = new CodeGenerator();
      Map<String, byte[]> classes = generator.generate(n, "TestBase");
      for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
        String fileName = entry.getKey() + ".class";
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
          fos.write(entry.getValue());
          System.out.println("Fichier " + fileName + " généré avec succès.");
        }
      }
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}