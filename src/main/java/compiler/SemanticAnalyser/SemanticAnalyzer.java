package compiler.SemanticAnalyser;

import compiler.Parser.*;

public class SemanticAnalyzer {

  private SymbolTable symbolTable = new SymbolTable();

  public void analyseTree(Node root){
    browse(root);
  }

  private void browse(Node node){
    if (node == null) return;

    if(node instanceof ProgramNode){
      browseProgramNode((ProgramNode) node);
    }

    else if(node instanceof AssignmentNode){
      browseAssignmentNode((AssignmentNode) node);
    }

    else if(node instanceof FunctionNode){
      browseFunctionNode((FunctionNode) node);
    }

    else if (node instanceof LocalBlockNode){
      browseLocalBlockNode((LocalBlockNode) node);
    }

    else if (node instanceof IfNode){
      browseIfNode((IfNode) node);
    }

    else if(node instanceof WhileNode){
      browseWhileNode((WhileNode) node);
    }

    else if(node instanceof DeclarationNode){
      browseDeclarationNode((DeclarationNode) node);
    }

    else if(node.getClass().getSimpleName().contains("CommentNode") ||
        node.getClass().getSimpleName().contains("CollectionDefNode") ||
        node.getClass().getSimpleName().contains("ForNode") ||
        node.getClass().getSimpleName().contains("ReturnNode")) {
      return;
    }

    else evaluateType(node);
  }

  private void browseProgramNode(ProgramNode programNode){
    for(Node node : programNode.getDeclarations()){
      browse(node);
    }
  }

  private void browseAssignmentNode(AssignmentNode assignmentNode) {
    String typeCoteGauche = "";
    String nomVariable = "";

    if (assignmentNode.getIdentifier() instanceof IdNode) {
      nomVariable = ((IdNode) assignmentNode.getIdentifier()).getName();
    } else {
      evaluateType(assignmentNode.getExpression());
      return;
    }

    if (!(assignmentNode.getType() instanceof EmptyNode)) {
      String typeDeclare = ((TypeNode) assignmentNode.getType()).getTypeName();
      if (!symbolTable.addNewVariable(nomVariable, typeDeclare, assignmentNode.isFinal())) {
        System.err.println("ScopeError");
        System.exit(2);
      }
      typeCoteGauche = typeDeclare;
    }
    else {
      String typeExistant = symbolTable.containsType(nomVariable);
      if (typeExistant == null) {
        System.err.println("ScopeError");
        System.exit(2);
      }
      if (symbolTable.variableIsFinal(nomVariable)) {
        System.err.println("ScopeError");
        System.exit(2);
      }

      typeCoteGauche = typeExistant;
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
        if (param instanceof ParameterNode) {
          ParameterNode paramNode = (ParameterNode) param;
          String paramType = paramNode.getType();
          String paramName = paramNode.getName();

          if (!symbolTable.addNewVariable(paramName, paramType, false)) {
            System.err.println("ScopeError");
            System.exit(2);
          }
        }
      }
    }
    if (functionNode.getBody() != null) {
      browse(functionNode.getBody());
    }
    symbolTable.removeScope();
  }

  private void browseLocalBlockNode(LocalBlockNode localBlockNode){
    symbolTable.addNewScope();
    for(Node node : localBlockNode.getLocalNodes()){
      browse(node);
    }
    symbolTable.removeScope();
  }

  private void browseIfNode(IfNode ifNode){
    String conditionType = evaluateType(ifNode.getCondition());
    if(!conditionType.equals("BOOL")){
      System.err.println("MissingConditionError");
      System.exit(2);
    }
    browse(ifNode.getThenCaseBlock());
    browse(ifNode.getElseCaseBlock());
  }

  private void browseWhileNode(WhileNode whileNode){
    String conditionType = evaluateType(whileNode.getCondition());
    if(!conditionType.equals("BOOL")){
      System.err.println("MissingConditionError");
      System.exit(2);
    }
    browse(whileNode.getCodeInNode());
  }

  private void browseDeclarationNode(DeclarationNode declarationNode){
    String type = declarationNode.getType();
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
    if (className.contains("AccessNode") || className.contains("Constructor")) return "UNKNOWN";

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
    throw new RuntimeException("Noeud non géré dans evaluateType : " + node.getClass().getSimpleName());
  }

}
