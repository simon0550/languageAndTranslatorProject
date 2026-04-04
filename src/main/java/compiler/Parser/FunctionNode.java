package compiler.Parser;

import java.util.ArrayList;
import java.util.List;

public class FunctionNode extends Node{
  private String name;
  private List<Node> parameters;
  private String retType;
  private Node body;

  public FunctionNode(String retType, String name, List<Node> parameters, Node body){
    this.name = name;
    this.parameters = parameters;
    this.retType = retType;
    this.body = body;
  }

  public String getName() {
    return name;
  }

  public List<Node> getParameters() {
    return parameters;
  }

  public String getRetType() {
    return retType;
  }

  public Node getBody() {
    return body;
  }

  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth)).append("FunctionNode(").append(retType).append(" ").append(name).append(")\n");
    if (parameters != null) {
      for (Node param : parameters) {
        if (param != null) {
          sb.append(param.print(depth + 1));
        }
      }
    }
    if (body != null) {
      sb.append(body.print(depth + 1));
    }

    return sb.toString();
  }
}
