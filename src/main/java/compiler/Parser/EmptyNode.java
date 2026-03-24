package compiler.Parser;

public class EmptyNode extends Node {


    @Override
    public String print(int depth) {
        return indent(depth) +"EmptyNode\n";
    }
}