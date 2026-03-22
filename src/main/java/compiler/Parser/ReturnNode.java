package compiler.Parser;


public class  ReturnNode extends Node{

  private Node expression;

  public ReturnNode(Node expression){
    this.expression = expression;
  }

  @Override
  public String toString() {
    return "ReturnNode{" +
        "expression=" + expression +
        '}';
  }
}