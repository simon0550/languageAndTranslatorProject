package compiler.Parser;

import java.util.List;

public class TableNode extends Node{
  private String name;
  private List<Node> values;

  public String getName() {
    return name;
  }

  public List<Node> getValues() {
    return values;
  }

  @Override
  public String toString() {
    return "TableNode{" +
        "name='" + name + '\'' +
        ", values=" + values +
        '}';
  }
}
