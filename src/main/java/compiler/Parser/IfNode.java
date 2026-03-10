package compiler.Parser;

public class IfNode extends Node{
  private Node condition;
  private Node thenCaseBlock;
  private Node elseCaseBlock;

  public IfNode(Node condition, Node elseCaseBlock, Node thenCaseBlock) {
    this.condition = condition;
    this.elseCaseBlock = elseCaseBlock;
    this.thenCaseBlock = thenCaseBlock;
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();

    sb.append("IfCondition\n");
    sb.append(condition.toString()).append("\n");

    sb.append("Then\n");
    sb.append(thenCaseBlock.toString());

    // On n'ajoute le bloc Else que s'il n'est pas vide
    if (!(elseCaseBlock instanceof EmptyNode)) {
      sb.append("\nElse\n");
      sb.append(elseCaseBlock.toString());
    }

    return sb.toString();
  }
}
