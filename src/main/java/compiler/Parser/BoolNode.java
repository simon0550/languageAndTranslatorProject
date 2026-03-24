package compiler.Parser;

public class BoolNode extends Node {
    private boolean value;
    public BoolNode(boolean value) {
        this.value = value;
    }


    @Override
    public String print(int depth) {
        return indent(depth) + "bool " + value + "\n";
    }
}
