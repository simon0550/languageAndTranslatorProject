package compiler.Parser;

import java.util.List;

public class ArrayLiteralNode extends Node {
  private List<Node> elements;

  public ArrayLiteralNode(List<Node> elements) {
    this.elements = elements;
  }

  @Override
  public String print(int depth) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent(depth)).append("ArrayLiteralNode\n");

    for (Node el : elements) {
      // Chaque élément du tableau est imprimé avec un niveau d'indentation de plus
      sb.append(el.print(depth + 1));
    }

    return sb.toString();
  }
}