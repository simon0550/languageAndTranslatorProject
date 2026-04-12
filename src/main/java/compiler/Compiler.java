package compiler;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.Node;
import compiler.SemanticAnalyzer.SemanticAnalyzer;

import java.io.FileReader;

public class Compiler {
    public static void main(String[] args) throws Exception {
        Lexer lexer = new Lexer(new FileReader(args[0]));
        Parser parser = new Parser(lexer);
        Node ast = parser.getAST();

        SemanticAnalyzer s = new SemanticAnalyzer();
        s.analyseTree(ast);
    }
}