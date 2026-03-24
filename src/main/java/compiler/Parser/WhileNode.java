package compiler.Parser;

public class WhileNode extends Node{
  private Node condition;
  private Node codeInNode;



  public WhileNode(Node condition, Node codeInNode) {
    this.condition = condition;
    this.codeInNode = codeInNode;
  }

  @Override
  public String toString() {
    return "WhileNode{" +
        "condition=" + condition +
        ", codeInNode=" + codeInNode +
        '}';
  }


}
