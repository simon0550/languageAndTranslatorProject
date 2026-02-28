package compiler.Lexer;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class Lexer {

    Reader reader;
    private int current;
    private Symbol last;

    private static final Set<String> KEYWORDS_SET = Set.of(
        "final", "coll", "def", "for", "while", "if", "else", "return", "not", "ARRAY"
    );

    private static final Map<String, String> UP_KEYWORDS = Map.of(
            "true", "BOOL",
            "false", "BOOL",
            "INT", "TYPE",
            "STRING", "TYPE",
            "DOUBLE", "TYPE",
            "FLOAT", "TYPE",
            "BOOL", "TYPE"
    );

    private static final Set<String> SYMBOLS = Set.of(
            "+", "-","/","*","%","==","=/=","<",">",">=","<=","(",")",
            "{","}","[","]",".","&&","||",";",",","="
    );


    public Lexer(Reader input) {
        this.reader = input;
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
        while (true) {
            if (current == -1) return null;
            if (Character.isWhitespace(current)) {
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
        if (isLetter(current) || current == '_') {
            return identifierKeyWordCollection();
        }
        // String
        if (current == '"') {
            return string();
        }
        // Number
        if (isDigit(current)) {
            return number();
        }
        // Symbol
        if(current != -1){
            return fetchSymbol();
        }

        return null;
    }

    private Symbol identifierKeyWordCollection() {
        StringBuilder stringBuilder = new StringBuilder();
        while (isLetter(current) || isDigit(current) || current == '_') {
            stringBuilder.append((char) current);
            nextChar();
        }

        String word = stringBuilder.toString();

        if(KEYWORDS_SET.contains(word)){
            last = new Symbol("KW_" + word.toUpperCase(),word);
            return last;
        }

        if (UP_KEYWORDS.containsKey(word)) {
            last = new Symbol(UP_KEYWORDS.get(word), word);
            return last;
        }

        if (Character.isUpperCase(word.charAt(0))) {
            last = new Symbol("COLLECTION", word);
            return last;
        }

        last = new Symbol("IDENTIFIER", word);
        return last;

    }

    private Symbol fetchSymbol() {
        StringBuilder sb = new StringBuilder();
        char first = (char) current;
        sb.append(first);
        nextChar();

        if (first == '=') {
            // == case
            if (current == '=') {
                sb.append((char) current);
                nextChar();
                // =/= case
            } else if (current == '/') {
                sb.append((char) current);
                nextChar();
                if (current == '=') {
                    sb.append((char) current);
                    nextChar();
                }
            }
        } else if (first == '<' || first == '>') {
            if (current == '=') {
                sb.append((char) current);
                nextChar();
            }
            // && case
        } else if (first == '&' && current == '&') {
            sb.append((char) current);
            nextChar();
            // || case
        } else if (first == '|' && current == '|') {
            sb.append((char) current);
            nextChar();
        }

        String result = sb.toString();
        if (SYMBOLS.contains(result)) {
            last = new Symbol("SYMBOL", result);
            return last;
        }

        throw new IllegalArgumentException("Symbole inconnu : " + result);
    }

    private boolean isLetter(int c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(int c) {
        return (c >= '0' && c <= '9');
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
        nextChar();

        while (current != '"' && current != -1) {
            stringBuilder.append((char) current);
            nextChar();
        }

        if (current == -1) {
            throw new RuntimeException("Syntax error");
        }
        nextChar();
        last = new Symbol("STRING", stringBuilder.toString());
        return last;
    }


}
