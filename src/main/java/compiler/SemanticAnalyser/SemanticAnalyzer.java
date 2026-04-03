package compiler.SemanticAnalyser;

import compiler.Parser.AssignmentNode;
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
      }
      return type;
    }

    return "";
  }

}
