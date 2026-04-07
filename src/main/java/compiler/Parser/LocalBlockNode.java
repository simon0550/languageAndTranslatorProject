package compiler.Parser;

import java.util.ArrayList;
import java.util.List;

public class LocalBlockNode extends Node{

  private List<Node> localNodes = new ArrayList<>();

  public List<Node> getLocalNodes() {
    return localNodes;
  }

  public void AddLocalNode(Node node){
    if(node != null){
      localNodes.add(node);
    }
  }

  @Override
  public String print(int depth) {
    String res = indent(depth) + "Block:\n";
    for (Node n : localNodes) {
      if (n != null && !(n instanceof EmptyNode)) {
        res += n.print(depth + 1);
      }
    }
    return res;
  }

}
