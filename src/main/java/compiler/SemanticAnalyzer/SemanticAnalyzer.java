package compiler.SemanticAnalyzer;

import compiler.Parser.*;

public class SemanticAnalyzer {

  private SymbolTable symbolTable = new SymbolTable();

  public void analyseTree(Node root){
    symbolTable.addNewScope();
    symbolTable.addNewVariable("read_INT", "INT", true);
    symbolTable.addNewVariable("println", "VOID", true);
    browse(root);
  }

  private void browse(Node node){
    if (node == null) return;

    if(node instanceof ProgramNode){
      for(Node decl : ((ProgramNode) node).getDeclarations()) browse(decl);
    }

    else if(node instanceof AssignmentNode){
      browseAssignmentNode((AssignmentNode) node);
    }

    else if(node instanceof FunctionNode){
      browseFunctionNode((FunctionNode) node);
    }

    else if (node instanceof LocalBlockNode){
      for(Node subNode : ((LocalBlockNode) node).getLocalNodes()) browse(subNode);
    }

    else if (node instanceof IfNode){
      browseIfNode((IfNode) node);
    }

    else if(node instanceof WhileNode){
      browseWhileNode((WhileNode) node);
    }

    else if(node instanceof ForNode){
      browseForNode((ForNode) node);
    }

    else if(node instanceof DeclarationNode){
      browseDeclarationNode((DeclarationNode) node);
    }

    else if(node instanceof ReturnNode){
      evaluateType(((ReturnNode)node).getExpression());
    }

    else if(node instanceof CollectionDeclarationNode) {
      browseCollectionDeclaration((CollectionDeclarationNode) node);
    }
    else if(node instanceof TableAssignmentNode) {
      browseTableAssignment((TableAssignmentNode) node);
    }
    else if(node.getClass().getSimpleName().contains("CommentNode")) {
      return;
    }
    else evaluateType(node);
  }

  private void browseCollectionDeclaration(CollectionDeclarationNode node) {
    String type = node.getType().toString();
    String name = ((IdNode) node.getName()).getName();

    if (symbolTable.isDeclaredInCurrentScope(name)) {
      System.err.println("ScopeError");
      System.exit(2);
    }

    symbolTable.addNewVariable(name, type + "[]", false);

    for (Node element : node.getElements()) {
      String elemType = evaluateType(element);
      if (!elemType.equals(type) && !elemType.equals("UNKNOWN")) {
        System.err.println("CollectionError");
        System.exit(2);
      }
    }
  }

  private void browseTableAssignment(TableAssignmentNode node) {
    String arrayName = node.getName().toString();
    String arrayType = symbolTable.containsType(arrayName);
    if (arrayType == null) {
      System.err.println("ScopeError");
      System.exit(2);
    }
    String indexType = evaluateType(node.getIndex());
    if (!indexType.equals("INT") && !indexType.equals("UNKNOWN")) {
      System.err.println("CollectionError");
      System.exit(2);
    }
    String valueType = evaluateType(node.getValue());
    String expectedType = arrayType.replace("[]", "").replace("ARRAY ", "");

    if (!valueType.equals(expectedType) && !valueType.equals("UNKNOWN")) {
      System.err.println("CollectionError");
      System.exit(2);
    }
  }

  private void browseAssignmentNode(AssignmentNode assignmentNode) {
    String nomVariable = "";

    if (assignmentNode.getIdentifier() instanceof IdNode) {
      nomVariable = ((IdNode) assignmentNode.getIdentifier()).getName();
    } else {
      evaluateType(assignmentNode.getExpression());
      return;
    }

    String typeCoteGauche = "";
    if (!(assignmentNode.getType() instanceof EmptyNode)) {
      typeCoteGauche = ((TypeNode) assignmentNode.getType()).getTypeName();
      if (!symbolTable.addNewVariable(nomVariable, typeCoteGauche, assignmentNode.isFinal())) {
        System.err.println("ScopeError");
        System.exit(2);
      }
    }
    else {
      typeCoteGauche = symbolTable.containsType(nomVariable);
      if (typeCoteGauche == null || symbolTable.variableIsFinal(nomVariable)) {
        System.err.println("ScopeError");
        System.exit(2);
      }
    }

    String typeCoteDroit = evaluateType(assignmentNode.getExpression());
    if (!typeCoteGauche.equals(typeCoteDroit) && !typeCoteDroit.equals("UNKNOWN")) {
      System.err.println("TypeError");
      System.exit(2);
    }
  }

  private void browseFunctionNode(FunctionNode functionNode) {
    symbolTable.addNewScope();
    if (functionNode.getParameters() != null) {
      for (Node param : functionNode.getParameters()) {
        ParameterNode paramNode = (ParameterNode) param;
        symbolTable.addNewVariable(paramNode.getName(), paramNode.getType(), false);
      }
    }
    browse(functionNode.getBody());
    symbolTable.removeScope();
  }

  private void browseIfNode(IfNode ifNode){
    String conditionType = evaluateType(ifNode.getCondition());
    if(!conditionType.equals("BOOL") && !conditionType.equals("UNKNOWN")){
      System.err.println("MissingConditionError");
      System.exit(2);
    }
    browse(ifNode.getThenCaseBlock());
    browse(ifNode.getElseCaseBlock());
  }

  private void browseWhileNode(WhileNode whileNode){
    String conditionType = evaluateType(whileNode.getCondition());
    if(!conditionType.equals("BOOL") && !conditionType.equals("UNKNOWN")){
      System.err.println("MissingConditionError");
      System.exit(2);
    }
    browse(whileNode.getCodeInNode());
  }

  private void browseForNode(ForNode forNode){
    symbolTable.addNewScope();

    browse(forNode.getInit());
    evaluateType(forNode.getStart());
    evaluateType(forNode.getEnd());
    evaluateType(forNode.getStep());
    browse(forNode.getBody());

    symbolTable.removeScope();
  }

  private void browseDeclarationNode(DeclarationNode declarationNode){
    String type = "";
    type = declarationNode.getType();

    String name = declarationNode.getName();
    if (symbolTable.isDeclaredInCurrentScope(name)) {
      System.err.println("ScopeError");
      System.exit(2);
    }
    symbolTable.addNewVariable(name, type, declarationNode.isFinal());
  }

  private String evaluateType(Node node){
    if(node == null || node instanceof EmptyNode) return "VOID";

    if(node instanceof IntNode) return "INT";
    if(node instanceof FloatNode) return "FLOAT";
    if(node instanceof StringNode) return "STRING";
    if (node instanceof BoolNode) return "BOOL";

    if(node instanceof IdNode){
      String name = ((IdNode) node).getName();
      String type = symbolTable.containsType(name);
      if(type == null){
        System.err.println("ScopeError");
        System.exit(2);
      }
      return type;
    }

    String className = node.getClass().getSimpleName();
    if (className.contains("ArrayNode")) return "UNKNOWN";
    if (className.contains("FunctionCallNode")) return "UNKNOWN";
    if (node instanceof TableAccessNode) {
      TableAccessNode accessNode = (TableAccessNode) node;
      String arrayName = String.valueOf(accessNode.getName()); // À adapter selon tes getters

      String arrayType = symbolTable.containsType(arrayName);
      if (arrayType == null) {
        System.err.println("ScopeError");
        System.exit(2);
      }

      String indexType = evaluateType(accessNode.getValue());
      if (!indexType.equals("INT") && !indexType.equals("UNKNOWN")) {
        System.err.println("CollectionError");
        System.exit(2);
      }
      return arrayType.replace("[]", "").replace("ARRAY ", "");
    }

    if(node instanceof BinaryNode){
      BinaryNode binaryNode = (BinaryNode) node;
      String leftType = evaluateType(binaryNode.getLeft());
      String rightType = evaluateType(binaryNode.getRight());
      String operator = binaryNode.getOperator();

      if (leftType.equals("UNKNOWN") || rightType.equals("UNKNOWN")) return "UNKNOWN";

      // Transformer un INT en FLOAT si l'un deux deux est FLOAT, INT sinon
      if(operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/") || operator.equals("%")){
        if((leftType.equals("INT") || leftType.equals("FLOAT")) && (rightType.equals("INT") || rightType.equals("FLOAT"))) {
          if(leftType.equals("FLOAT") || rightType.equals("FLOAT")) return "FLOAT";
          return "INT";
        }
        System.err.println("OperatorError");
        System.exit(2);
      }

      if(operator.equals("&&") || operator.equals("||")){
        if(leftType.equals("BOOL") && rightType.equals("BOOL")) return "BOOL";
        System.err.println("OperatorError");
        System.exit(2);
      }

      if (operator.equals("==") || operator.equals("=/=") || operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")) {
        if (leftType.equals(rightType)) return "BOOL";
        System.err.println("OperatorError");
        System.exit(2);
      }
    }
    return "UNKNOWN";
  }

}
