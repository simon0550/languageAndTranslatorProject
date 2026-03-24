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
  public String toString() {
    return "IfNode{" +
        "condition=" + condition +
        ", thenCaseBlock=" + thenCaseBlock +
        ", elseCaseBlock=" + elseCaseBlock +
        '}';
  }
}
