package compiler.Parser;

public class WhileNode extends Node{
  private Node condition;
  private Node codeInNode;



  public WhileNode(Node condition, Node codeInNode) {
    this.condition = condition;
    this.codeInNode = codeInNode;
  }

  @Override
  public String print(int depth) {
    String res = indent(depth) + "WhileNode:\n";
    if (condition != null) {
      res += condition.print(depth + 1);
    }
    if (codeInNode != null) {
      res += codeInNode.print(depth + 1);
    }

    return res;
  }


}
