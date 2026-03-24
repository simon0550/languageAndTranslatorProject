package compiler.Parser;

import java.util.List;

public class CollectionDefNode extends Node{

  private String name;
  private List<Node> properties;

  public CollectionDefNode(String name, List<Node> properties) {
    this.name = name;
    this.properties = properties;
  }

  @Override
  public String print(int depth) {
    return "";
  }
}
