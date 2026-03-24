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
                        "        current = current + max(iterations, 5) * 2;\n" +
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