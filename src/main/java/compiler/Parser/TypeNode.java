package compiler.Parser;

public class TypeNode extends Node{

  private String type;


  public TypeNode(String type) {
    this.type = type;
  }

  public String getTypeName() {
    return type;
  }


  @Override
  public String print(int depth) {
    return indent(depth) + "TypeNode(" + type + ")\n";
  }
}
