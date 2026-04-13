package compiler.SemanticAnalyzer;

import compiler.Parser.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer {

  private SymbolTable symbolTable = new SymbolTable();
  private String currentFunctionReturnType = null;
  private Map<String, List<String>> typeParamList = new HashMap<>(); // On stocker dans cette map les types de paramètres

  public void analyseTree(Node root){
    symbolTable.addNewScope();
    symbolTable.addNewVariable("read_INT", "INT", true);
    typeParamList.put("read_INT", new ArrayList<>());
    symbolTable.addNewVariable("println", "VOID", true);
    typeParamList.put("println", null);
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
      String expressionType = evaluateType(((ReturnNode)node).getExpression());
      if (currentFunctionReturnType != null) {
        if (!currentFunctionReturnType.equals(expressionType) && !expressionType.equals("UNKNOWN")) {
          System.err.println("ReturnError");
          System.exit(2);
        }
      }
    }

    else if(node instanceof CollectionDeclarationNode) {
      browseCollectionDeclaration((CollectionDeclarationNode) node);
    }
    else if(node instanceof CollectionDefNode) {
      browseCollectionDefNode((CollectionDefNode) node);
    }
    else if(node instanceof TableAssignmentNode) {
      browseTableAssignment((TableAssignmentNode) node);

    } else if (node instanceof  FunctionCallNode) {
      checkFunctionCall((FunctionCallNode) node);
    } else if(node.getClass().getSimpleName().contains("CommentNode")) {
      return;
    }
    else evaluateType(node);
  }

  private String checkFunctionCall(FunctionCallNode node) {
    String fname = node.getName();
    String returnType = symbolTable.containsType(fname);

    if (returnType == null) {
      System.err.println("ScopeError");
      System.exit(2);
    }

    List<String> expectedTypes = typeParamList.get(fname);
    List<Node> parameters = node.getParams();

    if (expectedTypes != null) {
      if (expectedTypes.size() != parameters.size()) {
        System.err.println("ArgumentError");
        System.exit(2);
      }

      for (int i = 0; i < expectedTypes.size(); i++) {
        String argType = evaluateType(parameters.get(i));
        String expected = expectedTypes.get(i);

        if (!argType.equals(expected) && !argType.equals("UNKNOWN")) {
          System.err.println("ArgumentError");
          System.exit(2);
        }
      }
    } else if (fname.equals("println")) {
      for (Node arg : parameters) evaluateType(arg);
    }

    return returnType;
  }

  private void browseCollectionDefNode(CollectionDefNode node) {
    String collName = node.getName();

    if (!Character.isUpperCase(collName.charAt(0))) {
      System.err.println("CollectionError");
      System.exit(2);
    }

    if (collName.equals("INT") || collName.equals("FLOAT") ||
            collName.equals("STRING") || collName.equals("BOOL") ||
            collName.equals("while") || collName.equals("if") ||
            collName.equals("def") || collName.equals("return") || collName.equals("coll")) {
      System.err.println("CollectionError");
      System.exit(2);
    }

    if (symbolTable.isDeclaredInCurrentScope(collName)) {
      System.err.println("CollectionError");
      System.exit(2);
    }

    symbolTable.addNewVariable(collName, collName, true);

    symbolTable.addNewScope();
    for (Node field : node.getProperties()) {
      browse(field);
    }
    symbolTable.removeScope();
  }

  private void browseCollectionDeclaration(CollectionDeclarationNode node) {
    String type = ((TypeNode) node.getType()).getTypeName();
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
    String name = functionNode.getName();
    String returnType = functionNode.getRetType();

    List<String> paramTypes = new ArrayList<>();
    if (functionNode.getParameters() != null) {
      for (Node parametre : functionNode.getParameters()) {
        ParameterNode paramNode = (ParameterNode) parametre;
        paramTypes.add(paramNode.getType());
      }
    }
    typeParamList.put(name, paramTypes);
    symbolTable.addNewVariable(name, returnType, true);

    String oldReturnType = currentFunctionReturnType;
    currentFunctionReturnType = returnType;

    symbolTable.addNewScope();
    if (functionNode.getParameters() != null) {
      for (Node param : functionNode.getParameters()) {
        ParameterNode paramNode = (ParameterNode) param;
        symbolTable.addNewVariable(paramNode.getName(), paramNode.getType(), false);
      }
    }

    browse(functionNode.getBody());
    symbolTable.removeScope();
    currentFunctionReturnType = oldReturnType;
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
    if (node instanceof ArrayLiteralNode) {
      ArrayLiteralNode arrayNode = (ArrayLiteralNode) node;
      if (arrayNode.getElements().isEmpty()) return "UNKNOWN[]";

      String firstElementType = evaluateType(arrayNode.getElements().get(0));

      for (Node element : arrayNode.getElements()) {
        String elemType = evaluateType(element);
        if (!elemType.equals(firstElementType) && !elemType.equals("UNKNOWN")) {
          System.err.println("CollectionError");
          System.exit(2);
        }
      }
      return firstElementType + "[]";
    }

    if (node instanceof ArrayNode) {
      ArrayNode arrNode = (ArrayNode) node;
      String sizeType = evaluateType(arrNode.getSize());
      if (!sizeType.equals("INT") && !sizeType.equals("UNKNOWN")) {
        System.err.println("CollectionError");
        System.exit(2);
      }
      return arrNode.getType() + "[]";
    }

    if (node instanceof FunctionCallNode){
      return checkFunctionCall((FunctionCallNode) node);
    }

    if (node instanceof TableAccessNode accessNode) {
      String arrayName = String.valueOf(accessNode.getName());

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
