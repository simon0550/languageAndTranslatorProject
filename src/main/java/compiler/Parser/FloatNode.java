package compiler.Parser;

public class FloatNode extends Node{

  private String value;

  public FloatNode(String value) {
    this.value = value;
  }


  @Override
  public String print(int depth) {
    return indent(depth) + "FloatNode(" + value + ")\n";
  }
}
