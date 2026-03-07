package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;

public class Parser {

  private Lexer lexer;
  private Symbol symbol;

  public Parser(Symbol symbol, Lexer lexer) {
    this.symbol = symbol;
    this.lexer = lexer;
  }

  public Node getAst(){
    return null;
  }
}
