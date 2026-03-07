package compiler.Parser;

public class TypeNode implements Node{

  private String type;

  public TypeNode(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Type, " + type;
  }
}
