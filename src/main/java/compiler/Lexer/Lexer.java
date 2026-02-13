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

    private void nextChar(){
        try {
            this.current = reader.read();
        }catch (IOException e){
            e.toString();
        }
    }
    
    public Symbol getNextSymbol() {
        // Handle space, comment and loop
        while (true){
            if(current == -1) return null;
            if(current == ' ' || current == '\t' || current == '\n'){
                nextChar();
                continue;
            }
            if(current == '#'){
                while (current != '\n' && current != -1){
                    nextChar();
                }
            }
            break;
        }
        nextChar();
        return null;
    }
}
