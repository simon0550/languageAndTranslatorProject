package compiler.SemanticAnalyser;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {

  // Pile pour les couches de scopes, mieux ?
  // Permet d'avoir des variables de même nom dans scopes différents
  private Stack<Map<String,String>> stackScopes = new Stack<>();

  // Etant donné que les variables peuvent avoir le même nom dans des scopes différents, on doit savoir lequel est final ou non
  private Stack<Map<String,Boolean>> stackFinals = new Stack<>();

  public SymbolTable() {
    this.stackScopes.push(new HashMap<>());
    this.stackFinals.push(new HashMap<>());
  }

  public void addNewScope(){
    stackScopes.push(new HashMap<>());
    stackFinals.push(new HashMap<>());
    // On pourrait rajouter les comportements ici ?
  }

  public void removeScope(){
    if(stackScopes.size() > 1){
      stackScopes.pop();
      stackFinals.pop();
    }
  }

  public void addNewVariable(String name, String type, boolean isFinal){
    stackScopes.peek().put(name,type);
    stackFinals.peek().put(name,isFinal);
  }

  public String containsType(String name){
    for(int i = stackScopes.size()-1; i >= 0; i--){
      if(stackScopes.get(i).containsKey(name)){
        return stackScopes.get(i).get(name);
      }
    }
    return null;
  }

  public boolean variableIsFinal(String name){
    for(int i = stackFinals.size()-1; i >= 0; i--){
      if(stackFinals.get(i).containsKey(name)){
        return stackFinals.get(i).get(name);
      }
    }
    return false;
  }

  public boolean isDeclaredInCurrentScope(String name){
    return stackScopes.peek().containsKey(name);
  }
}
