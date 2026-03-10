package compiler.Parser;

public class FloatNode extends Node{

  private float value;

  public FloatNode(float value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Float, " + value;
  }
}
