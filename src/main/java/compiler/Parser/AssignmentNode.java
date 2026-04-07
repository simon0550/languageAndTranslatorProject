package compiler.Parser;

public class AssignmentNode extends Node {
  private Node type;       
  private Node identifier; 
  private Node expression;
  private boolean isFinal;

  public AssignmentNode(Node type, Node identifier, Node expression, boolean isFinal) {
    this.type = type;
    this.identifier = identifier;
    this.expression = expression;
    this.isFinal = isFinal;
  }

  public Node getType() { return type; }
  public Node getIdentifier() { return identifier; }
  public Node getExpression() { return expression; }
  public boolean isFinal() { return isFinal; }


  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(indent(depth)).append("AssignmentNode");
    if (isFinal) {
      sb.append(" (FINAL)");
    }
    sb.append(":\n");

    sb.append(type.print(depth + 1));
    sb.append(identifier.print(depth + 1));
    sb.append(expression.print(depth + 1));
    return sb.toString();
  }
}
