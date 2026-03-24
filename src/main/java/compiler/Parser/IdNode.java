package compiler.Parser;

public class IdNode extends Node{
  private String name;

  public IdNode(String name) {
    this.name = name;
  }


  @Override
  public String print(int depth) {
    return indent(depth) + "IdNode(" + name +")\n";
  }

  public String getName() {
    return name;
  }
}
