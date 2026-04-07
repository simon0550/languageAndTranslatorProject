package compiler.Parser;

public class DotNode extends Node{

  private Node node;
  private String fieldName;

  public DotNode(Node node, String fieldName) {
    this.node = node;
    this.fieldName = fieldName;
  }

  @Override
  public String print(int depth) {
    return indent(depth) + "DotNode(." + fieldName + ")\n" +
        node.print(depth + 1);
  }
}
