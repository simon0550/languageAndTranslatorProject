package compiler.Parser;

public class ArrayNode extends Node {
  private String type;
  private Node size;

  public ArrayNode(String type, Node size) {
    this.type = type;
    this.size = size;
  }

  public String getType() {
    return type;
  }

  public Node getSize() {
    return size;
  }

  @Override
  public String print(int depth) {
    return indent(depth) + "ArrayNode(" + type + ")\n" +
        size.print(depth + 1);
  }
}