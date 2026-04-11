package compiler.Parser;


public class  ReturnNode extends Node{

  private Node expression;

  public ReturnNode(Node expression){
    this.expression = expression;
  }

  public Node getExpression() {
    return expression;
  }

  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth)).append("ReturnNode:\n");
    if (expression != null) {
      sb.append(expression.print(depth + 1));
    }
    return sb.toString();
  }
}