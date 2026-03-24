package compiler.Parser;

public class FloatNode extends Node{

  private String value;

  public FloatNode(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "FloatNode{" +
        "value='" + value + '\'' +
        '}';
  }
}
