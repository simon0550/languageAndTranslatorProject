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

  // Case of INT x = 5;
  public Node parseDeclarationType(){
    String typeString = consumeToken("TYPE");
    return endAssignment(new TypeNode(typeString));
  }

  // Case of x = 10;
  private Node parseAssignment(){
    return endAssignment(new EmptyNode());
  }

  private Node endAssignment(Node typeNode){
    String identifier = consumeToken("IDENTIFIER");
    Node newIdNode = new IdNode(identifier);

    consumeSymbol("=");
    Node valueAttributed = parseExpression();
    consumeSymbol(";");

    return new AssignmentNode(typeNode, newIdNode, valueAttributed);
  }


  private Node parseBlock(){
    consumeSymbol("{");
    LocalBlockNode localBlockNode = new LocalBlockNode();
    // While we stay in the local block, we add in the list
    while (symbol != null && !(symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals("}"))){
      Node parseBlock = parse();
      if(parseBlock != null) localBlockNode.AddLocalNode(parseBlock);
    }
    consumeSymbol("}");
    return localBlockNode;
  }


  private Node parseIf(){
    consumeToken("KW_IF");
    consumeSymbol("(");
    Node ifCondition = parseExpression();
    consumeSymbol(")");

    Node thenLocalBlock = parse();
    Node elseLocalBlock = new EmptyNode();

    if(symbol != null && symbol.getToken().equals("KW_ELSE")){
      consumeToken("KW_ELSE");
      elseLocalBlock = parse();
    }
    return new IfNode(ifCondition,thenLocalBlock,elseLocalBlock);
  }


  private Node parseWhile(){
    consumeToken("KW_WHILE");
    consumeSymbol("(");
    Node whileCondition = parseExpression();
    consumeSymbol(")");

    Node whileBody = parse();
    return new WhileNode(whileCondition,whileBody);
  }

  // Use of priority
  private Node parseExpression() {
    return parseOrExpression();
  }

  // Less priority than And (||)
  private Node parseOrExpression(){
    Node firstPartNode = parseAndExpression();
    while (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals("||")){
      String attributeOperation = symbol.getAttribute().toString();
      step();
      Node secondPartNode = parseAndExpression();
      firstPartNode = new BinaryNode(attributeOperation,firstPartNode,secondPartNode);
    }
    return firstPartNode;
  }

  // Less priority than Equality (&&)
  private Node parseAndExpression(){
    Node firstPartNode = parseEqualityExpression();
    while (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals("&&")){
      String attributeOperation = symbol.getAttribute().toString();
      step();
      Node secondPartNode = parseEqualityExpression();
      firstPartNode = new BinaryNode(attributeOperation,firstPartNode,secondPartNode);
    }
    return firstPartNode;
  }

  // Less priority than Relational (==,=/=)
  private Node parseEqualityExpression(){
    Node firstPartNode = parseRelationalExpression();
    while (symbol != null && symbol.getToken().equals("SYMBOL")
        && symbol.getAttribute().equals("==") || symbol.getAttribute().equals("=/=")){
      String attributeOperation = symbol.getAttribute().toString();
      step();
      Node secondPartNode = parseRelationalExpression();
      firstPartNode = new BinaryNode(attributeOperation,firstPartNode,secondPartNode);
    }
    return firstPartNode;
  }

  // Less priority than Add (<,>,<=,>=)
  private Node parseRelationalExpression(){
    return parseAddExpression();
  }

  // Less priority than Mul (+/-)
  private Node parseAddExpression(){
    Node firstPartNode = parseMulExpression();
    while (symbol != null && symbol.getToken().equals("SYMBOL") &&
        (symbol.getAttribute().equals("+") || symbol.getAttribute().equals("-"))) {
      String op = symbol.getAttribute().toString();
      step();
      Node secondPartNode = parseMulExpression();
      firstPartNode = new BinaryNode(op, firstPartNode, secondPartNode);
    }
    return firstPartNode;
  }

  // Less priority than Primary
  private Node parseMulExpression(){
    return parseFinalSymbol();
  }

  // The most priority
  private Node parseFinalSymbol(){
    String token = symbol.getToken();

    if (token.equals("NUMBER")) {
      String val = consumeToken("NUMBER");
      return new IntNode(val);
    }
    else if (token.equals("FLOAT")) {
      String val = consumeToken("FLOAT");
      return new FloatNode(val);
    }
    else if (token.equals("BOOL")) {
      String val = consumeToken("BOOL");
      return new BoolNode(Boolean.parseBoolean(val));
    }
    else if (token.equals("STRING")) {
      String val = consumeToken("STRING");
      return new StringNode(val);
    }

    throw new RuntimeException();
  }

}
