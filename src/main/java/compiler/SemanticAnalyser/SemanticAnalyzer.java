package compiler.SemanticAnalyser;

import compiler.Parser.AssignmentNode;
import compiler.Parser.BinaryNode;
import compiler.Parser.BoolNode;
import compiler.Parser.DeclarationNode;
import compiler.Parser.EmptyNode;
import compiler.Parser.FloatNode;
import compiler.Parser.IdNode;
import compiler.Parser.IfNode;
import compiler.Parser.IntNode;
import compiler.Parser.LocalBlockNode;
import compiler.Parser.Node;
import compiler.Parser.ProgramNode;
import compiler.Parser.StringNode;
import compiler.Parser.WhileNode;

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
    else evaluateType(node);
  }

  private void browseProgramNode(ProgramNode programNode){
    for(Node node : programNode.getDeclarations()){
      browse(node); // Appel récursif (on s'enfonce dans l'arbre)
    }
  }

  private void browseAssignmentNode(AssignmentNode assignmentNode){

  }

  private void browseLocalBlockNode(LocalBlockNode localBlockNode){

  }

  private void browseIfNode(IfNode ifNode){

  }

  private void browseWhileNode(WhileNode whileNode){

  }

  private void browseDeclarationNode(DeclarationNode declarationNode){
    String type = declarationNode.getType();
    String name = declarationNode.getName();

  }

  // Méthode qui renvoie le type du noeud à remonter
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

    if(node instanceof BinaryNode){
      BinaryNode binaryNode = (BinaryNode) node; // Récupère getter
      String leftType = evaluateType(binaryNode.getLeft()); // Renvoie un IdNode
      String rightType = evaluateType(binaryNode.getRight());
      String operator = binaryNode.getOperator();

      // Transformer un INT en FLOAT si l'un deux deux est FLOAT, INT sinon
      if(operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/") || operator.equals("%")){
        if(leftType.equals("INT") || leftType.equals("FLOAT") && rightType.equals("INT") || rightType.equals("FLOAT")) {
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
    throw new RuntimeException();
  }

}
