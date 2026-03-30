package compiler.SemanticAnalyser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {

  // Pile pour les couches de scopes, mieux ?
  // Permet d'avoir des variables de même nom dans scopes différents
  private Stack<Map<String,String>> stackScopes = new Stack<>();

  public SymbolTable() {
    this.stackScopes.push(new HashMap<>());
  }

  public void addNewScope(){
    stackScopes.push(new HashMap<>());
    // On pourrait rajouter les comportements ici ?
  }

  public void removeScope(){
    if(stackScopes.size() > 1){
      stackScopes.pop();
    }
  }
}
