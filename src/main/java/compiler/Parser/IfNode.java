package compiler.Parser;

public class IfNode extends Node{
  private Node condition;
  private Node thenCaseBlock;
  private Node elseCaseBlock;

  public IfNode(Node condition, Node thenCaseBlock,Node elseCaseBlock) {
    this.condition = condition;
    this.elseCaseBlock = elseCaseBlock;
    this.thenCaseBlock = thenCaseBlock;
  }


  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth)).append("IfNode:\n");
    sb.append(condition.print(depth + 1)).append("\n");
    sb.append(thenCaseBlock.print(depth+1)).append("\n");
    sb.append(elseCaseBlock.print(depth+1)).append("\n");
    return sb.toString();
  }
}
