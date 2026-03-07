package compiler.Parser;

public class AssignmentNode implements Node {
  private Node type;       // TypeNode
  private Node identifier; // IdentifierNode
  private Node expression; // Le BinaryOpNode ou autre valeur

  public AssignmentNode(Node type, Node identifier, Node expression) {
    this.type = type;
    this.identifier = identifier;
    this.expression = expression;
  }

  @Override
  public String toString() {
    // Ordre infixe avec le marqueur Expr au début
    return "Expr\n" +
        type.toString() + "\n" +
        identifier.toString() + "\n" +
        "AssignmentOperator\n" +
        expression.toString();
  }
}
