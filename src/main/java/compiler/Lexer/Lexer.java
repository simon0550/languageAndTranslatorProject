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
                continue;
            }
            break;
        }
        // Identifiers or keywords
        if(isLetter(current) || current == '_'){
            return identifierOrKeyWord();
        }

        nextChar();
        return null;
    }

    private Symbol identifierOrKeyWord(){
        return null;
    }

    private boolean isLetter(int c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(int c){
        return (c >= '0' && c <= '9');
    }

    private boolean isKeyWord(String word){
        String[] keyWords = {"final", "coll", "def", "for", "while", "if", "else", "return", "not", "ARRAY"};
        for(String key : keyWords){
            if(key.equals(word)) return true;
        }
        return false;
    }
}
