package compiler.Parser;

import java.util.List;

public class FunctionCallNode extends Node{
  private String name;
  private List<Node> params;

  public FunctionCallNode(String name, List<Node> params){
    this.name = name;
    this.params = params;
  }

  public String getName() {
    return name;
  }

  public List<Node> getParams() {
    return params;
  }

  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth)).append("FunctionCallNode: ").append(name).append("\n");
    if (params != null) {
      for (Node p : params) {
        if (p != null) {
          sb.append(p.print(depth + 1));
        }
      }
    }

    return sb.toString();
  }
}
