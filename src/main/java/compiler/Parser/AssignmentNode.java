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
  public String toString() {
    return "Expr\n" +
        type.toString() + "\n" +
        identifier.toString() + "\n" +
        "AssignmentOperator\n" +
        expression.toString();
  }
}
