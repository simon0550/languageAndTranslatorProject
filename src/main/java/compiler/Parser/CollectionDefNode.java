package compiler.Parser;

import java.util.List;

public class CollectionDefNode extends Node{

  private String name;
  private List<Node> properties;

  public CollectionDefNode(String name, List<Node> properties) {
    this.name = name;
    this.properties = properties;
  }

  public List<Node> getProperties() {
    return properties;
  }

  public String getName() {
    return name;
  }

  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth)).append("CollectionDefNode(").append(name).append(")\n");
    for (Node prop : properties) {
      sb.append(prop.print(depth + 1));
    }
    return sb.toString();
  }
}
