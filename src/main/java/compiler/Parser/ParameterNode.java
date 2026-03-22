package compiler.Parser;

public class ParameterNode extends Node{

  private String type;
  private String name;

  public ParameterNode(String type, String name ){
    this.type = type;
    this.name = name;

  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "ParameterNode{" +
        "type='" + type + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
