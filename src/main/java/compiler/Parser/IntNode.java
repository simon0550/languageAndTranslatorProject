package compiler.Parser;

public class IntNode implements Node{

  private String value;

  public IntNode(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Integer, " + value;
  }
}
