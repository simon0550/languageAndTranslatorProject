package compiler.SemanticAnalyser;

import compiler.Parser.DeclarationNode;
import compiler.Parser.Node;
import compiler.Parser.ProgramNode;

public class SemanticAnalyzer {
  private SymbolTable symbolTable;

  public SemanticAnalyzer() {
    this.symbolTable = new SymbolTable();
  }

  public void analyseTree(Node root){
    browse(root);
  }

  private void browse(Node node){
    if (node == null) return;

    if(node instanceof ProgramNode){
      browseProgramNode((ProgramNode) node);
    }
    if(node instanceof DeclarationNode){
      browseDeclarationNode((DeclarationNode) node);
    }
  }

  private void browseProgramNode(ProgramNode programNode){
    for(Node node : programNode.getDeclarations()){
      browse(node); // Appel récursif (on s'enfonce dans l'arbre)
    }
  }

  private void browseDeclarationNode(DeclarationNode declarationNode){
    String type = declarationNode.getType();
    String name = declarationNode.getName();

    if(symbolTable.containsVariable(name)){
      System.err.println("ScopeError");
      System.exit(1);
    }
  }
}
