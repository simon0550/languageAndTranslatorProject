package compiler.Parser;

public class WhileNode extends Node{
  private Node condition;
  private Node codeInNode;

  public WhileNode(Node condition, Node codeInNode) {
    this.condition = condition;
    this.codeInNode = codeInNode;
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();

    sb.append("WhileLoop\n");
    sb.append(condition.toString()).append("\n");

    sb.append("Do\n");
    sb.append(codeInNode.toString());

    return sb.toString();
  }
}
