package compiler.Parser;

import java.util.List;

public class FunctionCallNode extends Node{
  private String name;
  private List<Node> params;

  public FunctionCallNode(String name, List<Node> params){
    this.name = name;
    this.params = params;
  }

  @Override
  public String toString() {
    return "FunctionCallNode{" +
        "name='" + name + '\'' +
        ", params=" + params +
        '}';
  }
}
