package compiler.Parser;

public class AssignmentNode extends Node {
  private Node type;       
  private Node identifier; 
  private Node expression;



  public AssignmentNode(Node type, Node identifier, Node expression) {
    this.type = type;
    this.identifier = identifier;
    this.expression = expression;
  }


  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth)).append("AssignmentNode:\n");
    sb.append(type.print(depth + 1));
    sb.append(identifier.print(depth + 1));
    sb.append(expression.print(depth + 1));
    return sb.toString();
  }
}
