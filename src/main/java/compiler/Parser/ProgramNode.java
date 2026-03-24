package compiler.Parser;

import java.util.List;

public class ProgramNode extends Node {

    List<Node> declarations;

    public ProgramNode(List<Node> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String print(int depth) {
        StringBuilder res = new StringBuilder(indent(depth) + "ProgramNode:\n");
        for (Node n : declarations) {
            if (n != null && !(n instanceof EmptyNode)) {
                res.append(n.print(depth + 1));
            }
        }
        return res.toString();
    }
}
