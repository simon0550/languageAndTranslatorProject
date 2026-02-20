package compiler.Lexer;
import com.sun.tools.jconsole.JConsoleContext;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class Lexer {

    Reader reader;
    private int current;
    private Symbol last;
    private HashSet<String> identifiers;

    private static final Map<String, String> KEYWORDS = Map.of(
            "true", "BOOL",
            "false", "BOOL",
            "final", "MODIFIER",
            "int", "TYPE",
            "String", "TYPE",
            "double", " TYPE",
            "float", "TYPE"
    );

    private static final Set<String> SYMBOLS = Set.of(
            "+", "-","/","*","%","==","=/=","<",">",">=","<=","(,)",
            "{,}","[,]",".","&&","||",";",",","="

    );



    public Lexer(Reader input) {
        this.reader = input;
        this.identifiers = new HashSet<>();
        this.last = new Symbol("void");

        try {
            this.current = reader.read();
        } catch (IOException e) {
            e.toString();
        }
    }

    private void nextChar() {
        try {
            this.current = reader.read();
        } catch (IOException e) {
            e.toString();
        }
    }



    public Symbol getNextSymbol() {
        // Handle space, comment and loop
        while (true) {
            if (current == -1) return null;
            if (current == ' ' || current == '\t' || current == '\n') {
                nextChar();
                continue;
            }
            if (current == '#') {
                while (current != '\n' && current != -1) {
                    nextChar();
                }
                continue;
            }
            break;
        }
        // Identifiers or keywords
        if (isLetter(current) || current == '_' || isSymbol((char) current)) {
            return identifierKeyWordCollection();
        }
        if (current == '"') {
            return string();
        }
        if (isDigit(current) || current == '.') {
            return number();
        }
        nextChar();
        return null;
    }

    private Symbol identifierKeyWordCollection() {
        StringBuilder stringBuilder = new StringBuilder();
        while (isLetter(current) || isDigit(current) || current == '_' || isSymbol((char) current)) {
            stringBuilder.append((char) current);
            nextChar();
        }

        String word = stringBuilder.toString();
        if (isKeyWord(word)) {
            return new Symbol("KW_" + word.toUpperCase());
        }

        if (Character.isUpperCase(word.charAt(0))) {
            last = new Symbol("COLLECTION", word);
            return last;
        }

        if(KEYWORDS.containsKey(word)) {
            last = new Symbol(KEYWORDS.get(word), word);
            return last;
        }

        if(SYMBOLS.contains(word)) {
            return new Symbol("SYMBOL", word);
        }

        System.out.println("Identifiers " + identifiers);
        if(!identifiers.contains(word)) {
            if(!last.getToken().equals("TYPE") && !last.getToken().equals("COLLECTION")) {
                throw new IllegalArgumentException(word + " is not declared");
            }
            identifiers.add(word);
        }

        last = new Symbol("IDENTIFIER", word);
        return last;


    }

    private boolean isSymbol(char word) {
        return SYMBOLS.contains(String.valueOf(word));
    }

    private boolean isLetter(int c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(int c) {
        return (c >= '0' && c <= '9');
    }

    private boolean isKeyWord(String word) {
        String[] keyWords = {"final", "coll", "def", "for", "while", "if", "else", "return", "not", "ARRAY"};
        for (String key : keyWords) {
            if (key.equals(word)) return true;
        }
        return false;
    }

    public Symbol number() {
        boolean isFloat = false;
        StringBuilder stringBuilder = new StringBuilder();

        while (current == '0') {
            nextChar();
        }

        while (isDigit(current) || current == '.') {

            if (current == '.') {
                if (isFloat) {
                    throw new NumberFormatException("Multiple .");
                }
                isFloat = true;
            }

            stringBuilder.append((char) current);
            nextChar();
        }


        if (isFloat) {
            last = new Symbol("FLOAT", stringBuilder.toString());
            return last;
        } else {
            last = new Symbol("NUMBER", stringBuilder.toString());
            return last;
        }

    }


    public Symbol string() {
        StringBuilder stringBuilder = new StringBuilder();

        if (current == '"') {
            nextChar();
        }

        while (current != '"' && current != '\0') {
            stringBuilder.append((char) current);
            nextChar();
        }

        if (current == '\0') {
            throw new RuntimeException("Syntax error");
        }
        nextChar();
        last = new Symbol("STRING", stringBuilder.toString());
        return last;
    }


}
