package compiler.Parser;

import java.util.ArrayList;
import java.util.List;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
public class Parser {

  private Lexer lexer;
  private Symbol symbol;

  public Parser(Lexer lexer) {
    this.lexer = lexer;
    step();
  }

  public void step(){
    this.symbol = lexer.getNextSymbol();
  }

  public Node getAST() {
    List<Node> instructions = new ArrayList<>();
    while (this.symbol != null){
      instructions.add(parse());
      step();
    }
    return new ProgramNode(instructions);
  }

  private String consumeToken(String expectedToken) {
    if (symbol != null && symbol.getToken().equals(expectedToken)) {
      String output = (symbol.getAttribute() != null) ? symbol.getAttribute().toString() : "";
      step();
      return output;
    }
    throw new RuntimeException();
  }

  private void consumeSymbol(String expectedSymbol) {
    if (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals(expectedSymbol)) {
      step();
    }else{
      throw new RuntimeException();
    }
  }


  private Node parse(){
    String token = symbol.getToken();

    if (token.equals("TYPE")) {
      return parseDeclarationType();
    }

    else if (token.equals("SYMBOL") && symbol.getAttribute().equals("{")) {
      return parseBlock();
    }

    else if (token.equals("KW_IF")) {
      return parseIf();
    }

    else if (token.equals("KW_WHILE")) {
      return parseWhile();
    }

    else if (token.equals("IDENTIFIER")) {
      return parseAssignment();
    }

    if (token.equals("SYMBOL") && symbol.getAttribute().equals(";")) {
      step();
      return new EmptyNode();
    }

    throw new RuntimeException("Error");
  }


  public Node parseDeclarationType(){
    String typeString = consumeToken("TYPE");
    Node newTypeNode = new TypeNode(typeString);

    String identifier = consumeToken("IDENTIFIER");
    Node newIdNode = new IdNode(identifier);

    consumeSymbol("=");
    Node valueAttributed = parseExpression();
    consumeSymbol(";");
    return new AssignmentNode(newTypeNode,newIdNode,valueAttributed);
  }

  private Node parseBlock(){
    return null;
  }

  private Node parseIf(){
    consumeToken("KW_IF");
    consumeSymbol("(");
    Node ifCondition = parseExpression();
    consumeSymbol(")");

    Node thenLocalBlock = parse();
    Node elseLocalBlock = null;

    if(symbol != null && symbol.getToken().equals("KW_ELSE")){
      consumeToken("KW_ELSE");
      elseLocalBlock = parse();
    }
    return new IfNode(ifCondition,thenLocalBlock,elseLocalBlock);
  }

  private Node parseWhile(){
    return null;
  }

  private Node parseAssignment(){
    return null;
  }

  private Node parseExpression() {
    return parseOrExpression();
  }

  // Less priority than And ?
  private Node parseOrExpression(){
    return null;
  }

  // Less priority than Add ?
  private Node parseAndExpression(){
    return null;
  }

  private Node parseAdd(){
    return null;
  }

  private Node parseMul(){
    return null;
  }

}
