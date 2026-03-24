package compiler.Parser;

import java.lang.reflect.Field;
import java.util.List;

public abstract class Node {
  public Node(){

  }

  public String indent(int depth) {
    return "  ".repeat(depth);
  }

  public abstract String print(int depth);

  @Override
  public String toString() {
    return print(0);
  }

}
