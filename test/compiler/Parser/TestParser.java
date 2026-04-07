package compiler.Parser;

import compiler.Lexer.Lexer;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public class TestParser extends TestCase {

    public void testParser() {
        String code = "INT i = 3;\n"
                + "INT j = 3 * 5;\n"
                + "while (i > 0) {\n"
                + "    if (i == 3) {\n"
                + "        j = j + 1;\n"
                + "    } else {\n"
                + "        j = j - 1;\n"
                + "    }\n"
                + "    i = i - 1;\n"
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