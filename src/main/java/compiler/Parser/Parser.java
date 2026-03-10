package compiler.Parser;

import java.util.ArrayList;
import java.util.List;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Parser.IntNode;
import compiler.Parser.StringNode;
import compiler.Parser.ProgramNode;
public class Parser {

  private Lexer lexer;
  private Symbol symbol;

  public Parser(Symbol symbol, Lexer lexer) {
    this.symbol = symbol;
    this.lexer = lexer;
  }

  public Node getAST() {
    // Si on voit un type (int, float...), on traite une déclaration
    List<Node> instructions = new ArrayList<>();
    while (this.symbol != null){
      instructions.add(parse());
      step();
    }
    return new ProgramNode(instructions);
    
  
  }

  private Node parse(){
    // here, we have to put all our parser rules 
    if (this.symbol.getToken().equals("type")){
      return parseVariableDeclaration(this.symbol);
    }
    else return new EmptyNode();

  }

  // This only manges varible declaration and the values associated
  public Node parseVariableDeclaration(Symbol symbol){
    String type = symbol.getToken();
    step();
    String identifier = this.symbol.getToken();
    step();
    String assignmentOperator = this.symbol.getToken(); 
    if(!assignmentOperator.equals("="))
      throw new RuntimeException("Syntax error: expected '=' after variable name");
    step();
    Node expression = parseExpression(type);
    return new VariableDeclarationNode(type, identifier, expression);
  } 

  private Node parseExpression(String type) {
    if(type.equals ("int")){
      int value = Integer.parseInt(this.symbol.getAttribute().toString());
      return new IntNode(value);
    } else if (type.equals("float")){
      float value = Float.parseFloat(this.symbol.getAttribute().toString());
      return new FloatNode(value);
    } else if (type.equals("bool")){
      boolean value = Boolean.parseBoolean(this.symbol.getAttribute().toString());
      return new BoolNode(value);
    }
    return new EmptyNode();
    
  }


  

  public void step(){
    this.symbol = lexer.getNextSymbol();
  }


}
