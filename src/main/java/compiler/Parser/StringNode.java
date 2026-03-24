package compiler.Parser;

public class StringNode extends Node {

  private String content;

  public StringNode(String content) {
    this.content = content;
  }

  @Override
  public String print(int depth) {
    return indent(depth) + "StringNode(" + content + ")" + "\n";
  }
}
