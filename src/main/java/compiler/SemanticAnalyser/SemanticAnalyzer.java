package compiler.SemanticAnalyser;

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
  }

  private void browseProgramNode(ProgramNode programNode){
    for(Node node : programNode.getDeclarations()){
      browse(node); // Appel récursif (on s'enfonce dans l'arbre)
    }
  }
}
