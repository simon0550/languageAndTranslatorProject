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
    System.out.println("Exception on expected " + expectedToken + "Symbol token : "+ symbol.getToken());
    throw new RuntimeException();
  }


  private void consumeSymbol(String expectedSymbol) {
    if (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals(expectedSymbol)) {
      step();
    }else{
      System.out.println("Expected : " + expectedSymbol + " GOT : " +symbol.getAttribute());
      throw new RuntimeException();
    }
  }


  private Node parse(){
    String token = symbol.getToken();

    if (token.equals("KW_FINAL")) {
      consumeToken("KW_FINAL");
      return parseDeclarationType(true);
    }

    if (token.equals("TYPE")) {
      return parseDeclarationType(false);
    }

    else if (token.equals("KW_DEF")) {
      return parseDef();
    }

   else if (token.equals("KW_COLL")) {
      return parseCollectionDef();
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

    else if (token.equals("KW_FOR")) {
      return parseFor();
    }

    else if (token.equals("KW_RETURN")){
      return parseReturn();
    }

    else if (token.equals("IDENTIFIER")) {
      String name = consumeToken("IDENTIFIER");

      Node leftNode = new IdNode(name);
      if (symbol != null && symbol.getAttribute().equals("[")) {
        consumeSymbol("[");
        Node index = parseExpression();
        consumeSymbol("]");
        leftNode = new TableAccessNode((IdNode) leftNode, index);
      }

      while (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals(".")) {
        consumeSymbol(".");
        String propName = consumeToken("IDENTIFIER");
        leftNode = new DotAccessNode(leftNode, propName);
      }

      if (symbol != null && symbol.getAttribute().equals("(")) {
        return parseFunctionCall(name);
      } else if (symbol != null && symbol.getAttribute().equals("=")) {
        consumeSymbol("=");
        Node value = parseExpression();
        consumeSymbol(";");
        return new AssignmentNode(new EmptyNode(), leftNode, value);
      }
      return leftNode;
    }

    if (token.equals("SYMBOL") && symbol.getAttribute().equals(";")) {
      step();
      return new EmptyNode();
    }

    if(token.equals("COMMENT")){
      return new CommentNode(consumeToken("COMMENT"));
    }

    System.out.println("Le token courant est : " + symbol.getToken());
    throw new RuntimeException("Error");
  }

  private Node parseCollectionDef() {
    consumeToken("KW_COLL");
    String name = consumeToken("IDENTIFIER");
    consumeSymbol("{");
    List<Node> properties = new ArrayList<>();
    while (symbol != null && !(symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals("}"))) {
      properties.add(parseDeclarationType(false));
    }
    consumeSymbol("}");
    return new CollectionDefNode(name, properties);
  }

  private Node parseDef() {
    consumeToken("KW_DEF");
    String returnType = "VOID";
    if (symbol.getToken().equals("TYPE")) {
      returnType = consumeToken("TYPE");
    }
    String name = consumeToken("IDENTIFIER");
    return parseFunctionDeclaration(returnType, name);
  }

  private Node parseReturn() {
    step();
    Node exp = parseExpression();
    return new ReturnNode(exp);
  }

  private Node parseFor() {
    consumeToken("KW_FOR");
    consumeSymbol("(");
    Node init = parseExpression();
    consumeSymbol(";");
    Node start = parseExpression();
    consumeSymbol("->");
    Node end = parseExpression();
    consumeSymbol(";");
    Node step = parseExpression();
    consumeSymbol(")");
    Node body = parseBlock();
    return new ForNode(init, start, end, step, body);
  }

  public Node parseDeclarationType(boolean isFinal){
    String typeString = consumeToken("TYPE");

    if (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals("[")) {
      consumeSymbol("[");
      consumeSymbol("]");
      typeString += "[]";
    }

    String name = consumeToken("IDENTIFIER");

    if (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals(";")) {
      consumeSymbol(";");
      return new DeclarationNode(typeString, name, isFinal);
    }

    return endAssignment(new TypeNode(typeString), name);
  }

  private Node parseFunctionDeclaration(String typeString, String name) {
    consumeSymbol("(");
    ArrayList<Node> parameters = new ArrayList<>();
    if(!symbol.getAttribute().equals(")")){
      do {
        if(!parameters.isEmpty()) consumeSymbol(",");
        String type = consumeToken("TYPE");
        String pName = consumeToken("IDENTIFIER");
        parameters.add(new ParameterNode(type, pName));
      } while (symbol.getAttribute().equals(","));

    }
    consumeSymbol(")");
    Node funBody = parseBlock();
    return new FunctionNode(typeString, name, parameters, funBody);
  }

  private Node parseFunctionCall(String name) {
    List<Node> params = new ArrayList<>();
    consumeSymbol("(");
    if (!symbol.getAttribute().equals(")")) {
      do {
        if (!params.isEmpty()) consumeSymbol(",");
        params.add(parseExpression());

      } while (symbol.getAttribute().equals(","));
    }
    consumeSymbol(")");
    return new FunctionCallNode(name, params);
  }


  private Node endAssignment(Node typeNode, String name){
    Node newIdNode = new IdNode(name);

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

  private Node parseExpression() {
    return parseOrExpression();
  }

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

  private Node parseRelationalExpression(){
    Node firstPartNode = parseAddExpression();
    while (symbol != null && symbol.getToken().equals("SYMBOL") &&
        (symbol.getAttribute().equals("<") || symbol.getAttribute().equals(">") ||
            symbol.getAttribute().equals("<=") || symbol.getAttribute().equals(">="))) {
      String attributeOperation = symbol.getAttribute().toString();
      step();
      Node secondPartNode = parseAddExpression();
      firstPartNode = new BinaryNode(attributeOperation, firstPartNode, secondPartNode);
    }
    return firstPartNode;
  }

  private Node parseAddExpression(){
    Node firstPartNode = parseMulExpression();
    while (symbol != null && symbol.getToken().equals("SYMBOL") &&
        (symbol.getAttribute().equals("+") || symbol.getAttribute().equals("-"))) {
      String attributeOperation = symbol.getAttribute().toString();
      step();
      Node secondPartNode = parseMulExpression();
      firstPartNode = new BinaryNode(attributeOperation, firstPartNode, secondPartNode);
    }
    return firstPartNode;
  }

  private Node parseMulExpression(){
    Node firstPartNode = parseFinalSymbol();
    while (symbol != null && symbol.getToken().equals("SYMBOL") &&
        (symbol.getAttribute().equals("*") || symbol.getAttribute().equals("/")
            || symbol.getAttribute().equals("%"))) {
      String attributeOperation = symbol.getAttribute().toString();
      step();
      Node secondPartNode = parseFinalSymbol();
      firstPartNode = new BinaryNode(attributeOperation, firstPartNode, secondPartNode);
    }
    return firstPartNode;
  }

  private Node parseFinalSymbol(){
    String token = symbol.getToken();

    if (token.equals("IDENTIFIER")) {
      String name = consumeToken("IDENTIFIER");
      if (symbol != null && symbol.getAttribute().equals("(")) {
        return parseFunctionCall(name);
      }
      if (symbol != null && symbol.getAttribute().equals("[")) {
        consumeSymbol("[");
        Node index = parseExpression();
        consumeSymbol("]");
        return new TableAccessNode(new IdNode(name), index); // Assure-toi que cette classe existe !
      }
      return new IdNode(name);
    }

    if (token.equals("NUMBER")) {
      return new IntNode(consumeToken("NUMBER"));
    }
    else if (token.equals("FLOAT")) {
      return new FloatNode(consumeToken("FLOAT"));
    }
    else if (token.equals("BOOL")) {
      String val = consumeToken("BOOL");
      return new BoolNode(Boolean.parseBoolean(val));
    }
    else if (token.equals("STRING")) {
      return new StringNode(consumeToken("STRING"));
    }
    if (symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals("(")) {
      consumeSymbol("(");
      Node expr = parseExpression(); // On relance la machine récursivement
      consumeSymbol(")");
      return expr;
    }

    throw new RuntimeException("ParseFinalSymbol error");
  }


}
