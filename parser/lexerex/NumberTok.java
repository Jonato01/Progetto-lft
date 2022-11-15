package lexerex;
public class NumberTok extends Token {
  private final int val;

  public NumberTok (int val) {
    super(Tag.NUM);
    this.val = val;
  }

  public String toString() {
    String string = String.format("{tag: %3d, val: %d}", tag, val);
    return string;
}}