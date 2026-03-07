package compiler.Parser;

public class StringNode implements Node {

  private String content;

  public StringNode(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "String, " + content;
  }
}
