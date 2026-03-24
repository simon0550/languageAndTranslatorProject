package compiler.Parser;

public class CommentNode extends Node {

    private String comment;


    public CommentNode(String comment) {
        this.comment = comment;
    }


    @Override
    public String print(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(depth) + "CommentNode: \n" + indent(depth+1)  + comment + "\n");
        return sb.toString();
    }
}
