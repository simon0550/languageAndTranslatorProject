package compiler.Parser;

import java.util.List;

public class CollectionDeclarationNode extends Node{
  private Node name;
  private List<Node> elements;
  private Node type;
  public CollectionDeclarationNode(Node name, List<Node> elements, Node type) {
    this.name = name;
    this.elements = elements;
    this.type = type;
  }

  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth) + "CollectionNode:\n");
    sb.append(indent(depth + 1) + "\t" + name);
    sb.append(indent(depth + 1) + "\t" + type);
    sb.append(indent(depth + 2) + "\t" + "Elements:\n");
    for(Node n : elements) {
      sb.append(n.print(depth + 4));
    }
    return sb.toString();
  }
}
