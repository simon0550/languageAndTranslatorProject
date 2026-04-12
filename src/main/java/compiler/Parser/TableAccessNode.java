package compiler.Parser;

public class TableAccessNode extends Node {

    private Node name;
    private Node value;

    public TableAccessNode(Node name, Node value) {
        this.name = name;
        this.value = value;
    }

    public Node getName() {
        return name;
    }

    public Node getValue() {
        return value;
    }

    @Override
    public String print(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(depth) +  "TableAccessNode:\n");
        sb.append(name.print(depth + 1));
        sb.append(value.print(depth + 1));

        return sb.toString();
    }

}
