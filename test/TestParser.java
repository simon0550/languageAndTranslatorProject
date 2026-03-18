import compiler.Lexer.Lexer;
import compiler.Parser.Node;
import compiler.Parser.Parser;
import java.io.Reader;
import java.io.StringReader;

public class TestParser {
  public static void main(String[] args) {
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
}
