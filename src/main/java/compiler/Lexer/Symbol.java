package compiler.Lexer;

public class Symbol {
  private String token;
  private Object attribute;

  public Symbol(String token, Object attribute) {
    this.token = token;
    this.attribute = attribute;
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

  public Object getAttribute() {
    return attribute;
  }

  public String getToken() {
    return token;
  }
}
