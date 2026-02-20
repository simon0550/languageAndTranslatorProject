import compiler.Lexer.Symbol;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import compiler.Lexer.Lexer;

import static org.junit.Assert.*;

public class TestLexer {

    private Symbol getFirstSymbolFromString(String input) {
        Lexer lexer = new Lexer(new StringReader(input));
        return lexer.getNextSymbol();
    }

    private List<Symbol> getAllSymbolsFromString(String input) {
        Lexer lexer = new Lexer(new StringReader(input));
        List<Symbol> symbols = new ArrayList<>();

        Symbol current = lexer.getNextSymbol();

        while (current != null) {
            System.out.println(current);
            symbols.add(current);
            current = lexer.getNextSymbol();

        }
        return symbols;
    }

    @Test
    public void test() {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNotNull(lexer.getNextSymbol());
    }

    @Test
    public void testStringSimple() {
        String input = "\"ceci est un string\"";
        Lexer lexer = new Lexer(new StringReader(input));

        Symbol symbol = lexer.getNextSymbol();
        assertNotNull(symbol);

        assertEquals("STRING", symbol.getToken());
        assertEquals("ceci est un string", symbol.getAttribute());

    }

    @Test
    public void testNumberInteger() {
        String input = "56";
        Lexer lexer = new Lexer(new StringReader(input));

        Symbol symbol = lexer.getNextSymbol();
        assertNotNull(symbol);

        assertEquals("NUMBER", symbol.getToken());
        assertEquals("56",symbol.getAttribute());

    }
    @Test
    public void testNumberInteger2() {
        String input = "000056";
        Lexer lexer = new Lexer(new StringReader(input));

        Symbol symbol = lexer.getNextSymbol();

        assertNotNull(symbol);

        assertEquals("NUMBER", symbol.getToken());
        assertEquals("56",symbol.getAttribute());

    }

    @Test
    public void testNumberFloat() {
        String input = "56.56";
        Lexer lexer = new Lexer(new StringReader(input));

        Symbol symbol = lexer.getNextSymbol();
        assertNotNull(symbol);
        assertEquals("FLOAT", symbol.getToken());
        assertEquals("56.56", symbol.getAttribute());

    }

    @Test
    public void testNumberFloat2() {
        String input = ".56";
        Lexer lexer = new Lexer(new StringReader(input));

        Symbol symbol = lexer.getNextSymbol();
        assertNotNull(symbol);
        assertEquals("FLOAT", symbol.getToken());
        assertEquals(".56", symbol.getAttribute());

    }

    @Test
    public void testFloatException(){
        String input = "56.5.6";
        Lexer lexer = new Lexer(new StringReader(input));
        assertThrows(NumberFormatException.class, lexer::getNextSymbol);
    }

    @Test
    public void testBooleanTrue() {
        String input = "true";
        Lexer lexer = new Lexer(new StringReader(input));
        Symbol symbol = lexer.getNextSymbol();
        assertNotNull(symbol);
        assertEquals("BOOL", symbol.getToken());
        assertEquals("true",symbol.getAttribute());

    }


    @Test
    public void complexTest(){
        String input = "List<String> test == 2; test == 3";
        List<Symbol> symbols = getAllSymbolsFromString(input);
        System.out.println(symbols);
    }
}
