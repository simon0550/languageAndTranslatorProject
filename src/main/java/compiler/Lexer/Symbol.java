package compiler.Lexer;

public class Symbol {
  private String token;
  private Object attribute;

  public Symbol(Object attribute, String token) {
    this.attribute = attribute;
    this.token = token;
  }

  public Symbol(String token) {
    this.token = token;
  }

  @Override
  public String toString() {
    if(attribute != null){
      return "<" + token + "," + attribute + ">";
    }else return "<" + token + ">";
  }
}
