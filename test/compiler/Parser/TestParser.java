package compiler.Parser;

import compiler.Lexer.Lexer;
import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;

public class TestParser extends TestCase {

    public void testParser() {
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
            + "                write(message);\n"
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

        System.out.println("AST Résultat :");
        System.out.println(ast.toString());
    }

    public void testParser3_ComplexCode() {
        String code =
                "# Good luck\n" +
                        "\n" +
                        " INT i = 3;\n" +
                        " FLOAT j = 3.2*5.0;\n" +
                        " INT k = i*3;\n" +
                        " STRING message = \"Hello\";\n" +
                        " BOOL isEmpty  = true;\n" +
                        "\n" +
                        
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
                        "}";

        try {
            Reader reader = new StringReader(code);
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(lexer);

            Node ast = parser.getAST();

            System.out.println("AST GÉNÉRÉ AVEC SUCCÈS ! Voici l'arbre :\n");
            System.out.println(ast.toString());

        } catch (Exception e) {
            System.err.println("ERREUR DE PARSING : " + e.getMessage());
            e.printStackTrace();
        }
    }

}