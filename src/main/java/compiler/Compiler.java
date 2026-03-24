package compiler;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.Node;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {

        Lexer lexer = new Lexer(new FileReader(args[1]));

        if (args[0].equals("-lexer")) {
            while (true) {
                var symbol = lexer.getNextSymbol();
                if (symbol == null) break;
                System.out.println(symbol);
            }
        }
        else if (args[0].equals("-parser")) {
            Parser parser = new Parser(lexer);
            Node ast = parser.getAST();
            if (ast != null) {
                System.out.print(ast.print(0));
            }
        }
    }
}