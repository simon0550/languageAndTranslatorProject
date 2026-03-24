package compiler.Parser;

public class DeclarationNode extends Node{

  private String typeString;
  private String name;
  private boolean isFinal;

  public DeclarationNode(String typeString, String name, boolean isFinal) {
    this.typeString = typeString;
    this.name = name;
    this.isFinal = isFinal;
  }

  @Override
  public String print(int depth) {
    return "";
  }
}
