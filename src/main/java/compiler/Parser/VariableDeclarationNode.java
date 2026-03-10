package compiler.Parser;

public class VariableDeclarationNode extends Node{
  String type;
  String name;
  Node value;

  public VariableDeclarationNode(String type, String name, Node value){
    this.name=name;
    this.type=type;
    this.value = value;
  }

  @Override
  public String toString() {
    return "Declaration(Type: " + type + ", Name: " + name + ", Value: [" + value.toString() + "])";
  }

}
