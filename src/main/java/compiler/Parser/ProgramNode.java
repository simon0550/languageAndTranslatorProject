package compiler.Parser;

import java.util.List;

public class ProgramNode extends Node {

    List<Node> declarations;

    public ProgramNode(List<Node> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node n : declarations) {
            if (n != null) {
                sb.append(n).append("\n");
            }
        }
        return sb.toString().trim();
    }
}
