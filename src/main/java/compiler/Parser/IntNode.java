package compiler.Parser;

public class IntNode extends Node{

  private int value;

  public IntNode(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Integer, " + value;
  }
}
