package compiler.Parser;

public class BinaryNode extends Node {
  private String operator;
  private Node left;
  private Node right;

  public BinaryNode(String operator, Node left, Node right) {
    this.operator = operator;
    this.left = left;
    this.right = right;
  }

  private String getOperatorCategory() {
    switch (operator) {
      case "+":
        case "-":
          case "*":
            case "/":
              case "%":
        return "ArithmeticOperator";
      case "==":
        case "=/=":
          case "<":
            case ">":
              case "<=":
                case ">=":
        return "RelationalOperator";
      case "&&":
        case "||":
        return "LogicalOperator";
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public String toString() {
    return "Expr\n" +
        left.toString() + "\n" +
        getOperatorCategory() + ", " + operator + "\n" +
        right.toString();
  }
}
