package compiler.Parser;

import java.util.ArrayList;
import java.util.List;

public class LocalBlockNode extends Node{

  private List<Node> localNodes = new ArrayList<>();

  public void AddLocalNode(Node node){
    if(node != null){
      localNodes.add(node);
    }
  }

  public String toString(){
    StringBuilder stringBuilder = new StringBuilder();
    for(Node node : localNodes){
      stringBuilder.append(node.toString());
    }
    return stringBuilder.toString().trim();
  }
}
