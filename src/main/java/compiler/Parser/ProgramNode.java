package compiler.Parser;

import java.util.List;

public class ProgramNode extends Node {

    List<Node> declarations;

    public ProgramNode(List<Node> declarations) {
        this.declarations = declarations;
    }
}
