package compiler.Parser;

public class IdNode implements Node{
  private String value;

  public IdNode(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Identifier, " + value;
  }
}
