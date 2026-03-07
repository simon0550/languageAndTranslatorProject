package compiler.Parser;

public class FloatNode implements Node{

  private String value;

  public FloatNode(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Float, " + value;
  }
}
