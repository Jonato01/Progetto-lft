public class NumberTok extends Token {
	public String lexeme;

	public NumberTok(String s) {
		super(Tag.NUM);
		lexeme = s;
	}

	public String toString() {
		return "<" + tag + ", " + lexeme + ">";
	}
}