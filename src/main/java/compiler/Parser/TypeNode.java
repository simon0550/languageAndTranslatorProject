package compiler.Parser;

public class TypeNode extends Node{

  private String type;

  public String getTypeName() {
    return type;
  }

  public TypeNode(String type) {
    this.type = type;
  }


  @Override
  public String print(int depth) {
    return indent(depth) + "TypeNode(" + type + ")\n";
  }
}
