package compiler.Parser;

public class IntNode extends Node{

  private String value;

  public IntNode(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String print(int depth) {
    return indent(depth) + "IntNode(" + value + ")\n";
  }
}
