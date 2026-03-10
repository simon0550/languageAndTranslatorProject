package compiler.Parser;

public class IdNode extends Node{
  private String name;

  public IdNode(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Identifier, " + name;
  }
}
