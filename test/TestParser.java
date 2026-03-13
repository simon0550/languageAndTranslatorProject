import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Parser.Node;
import compiler.Parser.Parser;
import java.io.Reader;
import java.io.StringReader;

public class TestParser {
  public static void main(String[] args) {
    String code = "int i = 3;";
    Reader reader = new StringReader(code);
    Lexer lexer = new Lexer(reader);
    Parser parser = new Parser(lexer);
    Node ast = parser.getAST();

    System.out.println("AST Résultat :");
    System.out.println(ast.toString());
  }
}
