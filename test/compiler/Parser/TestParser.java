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
                "INT seuil = 100;\n" +
                        "BOOL isRunning = true;\n" +
                        "\n" +
                        "INT max(INT x, INT y) {\n" +
                        "    if (x >= y) {\n" +
                        "        RETURN x;\n" +
                        "    } else {\n" +
                        "        RETURN y;\n" +
                        "    }\n" +
                        "}\n" +
                        "\n" +
                        "INT process(INT startValue) {\n" +
                        "    INT current = startValue;\n" +
                        "    INT iterations = 0;\n" +
                        "    \n" +

                        "    # ceci est une boucle while \n " +
                        "while (current < seuil && isRunning == true) {\n" +
                        "        current = (current + max(iterations, 5)) * 2;\n" +
                        "        iterations = iterations + 1;\n" +
                        "        \n" +
                        "        if (iterations > 50 || current < 0) {\n" +
                        "            isRunning = false;\n" +
                        "        }\n" +
                        "    }\n" +
                        "    RETURN current;\n" +
                        "}\n" +
                        "\n" +
                        "INT resultatFinal = process(10);\n" +
                        "ARRAY FLOAT table = [0.1,2.0,4.0,6.0];\n" +
                        "INT a = table[1];\n"+
                        "table[5] = 1+1;\n"+
                        "print(\"Le resultat est : \", resultatFinal);\n";

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