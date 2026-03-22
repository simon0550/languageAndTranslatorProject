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

  @Override
  public String toString() {
    return "FunctionNode{" +
        "name='" + name + '\'' +
        ", parameters=" + parameters +
        ", retType='" + retType + '\'' +
        ", body=" + body +
        '}';
  }
}
