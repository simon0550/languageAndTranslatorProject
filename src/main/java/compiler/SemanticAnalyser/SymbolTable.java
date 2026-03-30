package compiler.SemanticAnalyser;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
  private Map<String, String> mapScopes;

  public SymbolTable() {
    this.mapScopes = new HashMap<>();
  }
}
