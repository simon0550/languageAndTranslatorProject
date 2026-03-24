package compiler.Parser;

public class TableAssignmentNode extends Node {
    private Node name;
    private Node index;
    private Node value;

    public TableAssignmentNode(Node name, Node index, Node value) {
        this.name = name;
        this.index = index;
        this.value = value;
    }

    @Override
    public String print(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(depth)).append("TableAssignmentNode:\n").append(name.print(depth + 2));
        sb.append(indent(depth + 1)).append("Index:\n");
        if (index != null) {
            sb.append(index.print(depth + 2));
        }

        sb.append(indent(depth + 1)).append("Value:\n");
        if (value != null) {
            sb.append(value.print(depth + 2));
        }
        return sb.toString();
    }
}
