package compiler.Parser;

public class ForNode extends Node{

  private Node init;
  private Node start;
  private Node end;
  private Node step;
  private Node body;

  public ForNode(Node init, Node start, Node end, Node step, Node body) {
    this.init = init;
    this.start = start;
    this.end = end;
    this.step = step;
    this.body = body;
  }

  @Override
  public String print(int depth) {
    return "";
  }
}
