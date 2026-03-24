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
    if (symbol != null && symbol.getAttribute().equals(expectedSymbol)) {
      if (symbol.getToken().equals("SYMBOL") || (expectedSymbol.equals(".") && symbol.getToken().equals("FLOAT"))) {
        step();
        return;
      }
    }
    throw new RuntimeException();
  }


  private Node parse(){
    if (symbol == null) return new EmptyNode();
    String token = symbol.getToken();

    if (token.equals("KW_FINAL")) {
      step();
      return parseDeclarationType(true);
    }

    if (token.equals("TYPE") || token.equals("COLLECTION") || token.equals("KW_ARRAY")) {
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

    else if (token.equals("KW_RETURN") || (token.equals("IDENTIFIER") && symbol.getAttribute().toString().equalsIgnoreCase("return"))){
      return parseReturn();
    }

    if (token.equals("IDENTIFIER")) {
      String name = consumeToken("IDENTIFIER");
      Node left = new IdNode(name);

      left = parseSuffixes(left);

      if (symbol != null && symbol.getAttribute().equals("=")) {
        consumeSymbol("=");
        Node val = parseExpression();
        consumeSymbol(";");
        return new AssignmentNode(new EmptyNode(), left, val);
      }

      if (left instanceof FunctionCallNode || left instanceof DotNode) {
        if (symbol != null && symbol.getAttribute().equals(";")) consumeSymbol(";");
        return left;
      }
    }

    if (token.equals("SYMBOL") && symbol.getAttribute().equals(";")) {
      step();
      return new EmptyNode();
    }

    if(token.equals("COMMENT")){
      return new CommentNode(consumeToken("COMMENT"));
    }

    return parseExpression();
  }

  private Node parseSuffixes(Node left) {
    while (symbol != null) {
      if (symbol.getAttribute().equals("[")) {
        consumeSymbol("[");
        Node index = parseExpression();
        consumeSymbol("]");
        left = new TableAccessNode((IdNode) (left instanceof IdNode ? left : new IdNode(left.toString())), index);
      } else if (symbol.getAttribute().equals(".")) {
        consumeSymbol(".");
        String field = consumeToken("IDENTIFIER");
        left = new DotNode(left, field);
      } else if (symbol.getAttribute().equals("(")) {
        List<Node> args = parseArguments();
        left = new FunctionCallNode(left instanceof IdNode ? ((IdNode)left).getName() : left.toString(), args);
      } else {
        break;
      }
    }
    return left;
  }

  private Node parseCollectionDef() {
    consumeToken("KW_COLL");
    String name = consumeToken("COLLECTION");
    consumeSymbol("{");
    List<Node> properties = new ArrayList<>();
    while (symbol != null && !symbol.getAttribute().equals("}")) {
      properties.add(parse());
    }
    consumeSymbol("}");
    return new CollectionDefNode(name, properties);
  }

  private Node parseDef() {
    step();
    String returnType = "VOID";
    if (symbol.getToken().equals("TYPE") || symbol.getToken().equals("COLLECTION")) {
      returnType = symbol.getAttribute().toString();
      step();
    }
    String name = consumeToken("IDENTIFIER");
    return parseFunctionDeclaration(returnType, name);
  }

  private Node parseReturn() {
    step();
    Node exp = parseExpression();
    consumeSymbol(";");
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
    String type = symbol.getAttribute().toString();
    step();

    if (type.equals("ARRAY") && symbol != null && (symbol.getToken().equals("TYPE") || symbol.getToken().equals("COLLECTION"))) {
      type = type + " " + symbol.getAttribute().toString();
      step();
    }

    if (symbol != null && symbol.getAttribute().equals("[")) {
      consumeSymbol("[");
      consumeSymbol("]");
      type += "[]";
    }

    String name = consumeToken("IDENTIFIER");

    if (symbol != null && symbol.getAttribute().equals("(")) {
      return parseFunctionDeclaration(type, name);
    }

    if (symbol != null && symbol.getAttribute().equals(";")) {
      consumeSymbol(";");
      return new DeclarationNode(type, name, isFinal);
    }

    return endAssignment(new TypeNode(type), name);
  }

  private Node parseFunctionDeclaration(String type, String name) {
    consumeSymbol("(");
    List<Node> params = new ArrayList<>();
    if (!symbol.getAttribute().equals(")")) {
      do {
        if (!params.isEmpty() && symbol.getAttribute().equals(",")) consumeSymbol(",");
        String pType = symbol.getAttribute().toString();
        step();
        if (symbol.getAttribute().equals("[")) { consumeSymbol("["); consumeSymbol("]"); pType+="[]"; }
        String pName = consumeToken("IDENTIFIER");
        params.add(new ParameterNode(pType, pName));
      } while (symbol.getAttribute().equals(","));
    }
    consumeSymbol(")");
    Node body = parseBlock();
    return new FunctionNode(type, name, params, body);
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

  private List<Node> parseArguments() {
    consumeSymbol("(");
    List<Node> args = new ArrayList<>();
    if (!symbol.getAttribute().equals(")")) {
      do {
        if (!args.isEmpty() && symbol.getAttribute().equals(",")) consumeSymbol(",");
        args.add(parseExpression());
      } while (symbol.getAttribute().equals(","));
    }
    consumeSymbol(")");
    return args;
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
        && (symbol.getAttribute().equals("==") || symbol.getAttribute().equals("=/="))){
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

    if (token.equals("TYPE")) {
      String type = consumeToken("TYPE");
      consumeToken("KW_ARRAY");
      consumeSymbol("[");
      Node size = parseExpression();
      consumeSymbol("]");
      return new ArrayNode(type, size);
    }

    if (token.equals("IDENTIFIER") || token.equals("COLLECTION")) {
      String name = (token.equals("COLLECTION")) ? consumeToken("COLLECTION") : consumeToken("IDENTIFIER");

      Node leftNode = new IdNode(name);

      if (symbol != null && symbol.getAttribute().equals("(")) {
        return parseFunctionCall(name);
      }
      else if (symbol != null && symbol.getAttribute().equals("[")) {
        consumeSymbol("[");
        Node index = parseExpression();
        consumeSymbol("]");
        leftNode = new TableAccessNode(new IdNode(name), index);
      }
      while (symbol != null && symbol.getToken().equals("SYMBOL") && symbol.getAttribute().equals(".")) {
        consumeSymbol(".");
        String propName = consumeToken("IDENTIFIER");
        leftNode = new DotNode(leftNode, propName);
      }
      return leftNode;
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
      Node expr = parseExpression();
      consumeSymbol(")");
      return expr;
    }

    throw new RuntimeException("ParseFinalSymbol error");
  }


}
