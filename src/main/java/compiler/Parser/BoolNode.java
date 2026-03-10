package compiler.Parser;

public class BoolNode extends Node {
    private boolean value;
    public BoolNode(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Bool, " + value;
    }
}
