package compiler.Parser;

public class TypeNode extends Node{

  private String type;

  public TypeNode(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "TypeNode{" +
        "type='" + type + '\'' +
        '}';
  }
}
