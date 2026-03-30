package compiler.Parser;

public class DeclarationNode extends Node{

  private String type;
  private String name;
  private boolean isFinal;

  public DeclarationNode(String type, String name, boolean isFinal) {
    this.type = type;
    this.name = name;
    this.isFinal = isFinal;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public String print(int depth) {
    String finalStr = isFinal ? "final " : "";
    return indent(depth) + "DeclarationNode(" + finalStr + type + " " + name + ")\n";
  }
}
