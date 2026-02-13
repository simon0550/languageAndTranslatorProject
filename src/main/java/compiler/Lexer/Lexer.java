package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;

public class Lexer {

    Reader reader;
    private int current;

    public Lexer(Reader input) {
        this.reader = input;
        try {
            this.current = reader.read();
        }catch (IOException e){
            e.toString();
        }
    }
    
    public Symbol getNextSymbol() {
        return null;
    }
}
