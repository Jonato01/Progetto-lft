import java.io.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    private String getWord(BufferedReader br) {
        String exit = "";
        while (Character.isLetterOrDigit(peek) || peek == '_') {
            exit += peek;
            readch(br);
        }
        return exit;
    }

    private String getNum(BufferedReader br) {
        String exit = "";
        while (Character.isDigit(peek)) {
            exit += peek;
            readch(br);
        }
        if (Character.isLetter(peek)) {
            return null;
        }
        return exit;
    }

    public Token lexical_scan(BufferedReader br) {
        try {
            while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
                if (peek == '\n')
                    line++;
                readch(br);
            }

            switch (peek) {
                case '!':
                    peek = ' ';
                    return Token.not;

                case '(':
                    peek = ' ';
                    return Token.lpt;

                case ')':
                    peek = ' ';
                    return Token.rpt;

                case '[':
                    peek = ' ';
                    return Token.lpq;

                case ']':
                    peek = ' ';
                    return Token.rpq;

                case '{':
                    peek = ' ';
                    return Token.lpg;

                case '}':
                    peek = ' ';
                    return Token.rpg;

                case '+':
                    peek = ' ';
                    return Token.plus;

                case '-':
                    peek = ' ';
                    return Token.minus;

                case '*':
                    peek = ' ';
                    return Token.mult;

                case '/':
                    readch(br);
                    if (peek == '*') {
                        while (true) {
                            readch(br);
                            if (peek == '*') {
                                readch(br);
                                if (peek == '/') {
                                    readch(br);
                                    return lexical_scan(br);
                                }
                            } else if (peek == (char) -1) {
                                System.err.println("EOF reached.");
                                return null;
                            }
                        }
                    } else if (peek == '/') {
                        while (true) {
                            readch(br);
                            if (peek == '\n') {
                                readch(br);
                                return lexical_scan(br);
                            } else if (peek == (char) -1) {
                                return null;
                            }
                        }

                    } else {
                        return Token.div;
                    }
                case ';':
                    peek = ' ';
                    return Token.semicolon;

                case ',':
                    peek = ' ';
                    return Token.comma;

                // FATTO ... gestire i casi di ( ) [ ] { } + - * / ; , ... //

                case '&':
                    readch(br);
                    if (peek == '&') {
                        peek = ' ';
                        return Word.and;
                    } else {
                        System.err.println("Erroneous character"
                                + " after & : " + peek + " 103");
                        return null;
                    }

                case '|':
                    readch(br);
                    if (peek == '|') {
                        peek = ' ';
                        return Word.or;
                    } else {
                        System.err.println("Erroneous character"
                                + " after | : " + peek + " 114");
                        return null;
                    }

                case '<':
                    peek = ' ';
                    readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.le;
                    } else if (peek == '>') {
                        peek = ' ';
                        return Word.ne;
                    }
                    return Word.lt;

                case '>':
                    peek = ' ';
                    readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.ge;
                    }
                    return Word.gt;

                case '=':
                    peek = ' ';
                    readch(br);
                    if (peek == '=') {
                        peek = ' ';
                        return Word.eq;
                    } else {
                        System.err.println("Erroneous character"
                                + " after = : " + peek);
                        return null;
                    }
                    // FATTO ... gestire i casi di || < > <= >= == <> ... //

                case (char) -1:
                    return new Token(Tag.EOF);

                default:
                    if (Character.isLetter(peek)) {
                        String str = getWord(br);
                        if (str.compareTo("assign") == 0) {
                            str = " ";
                            return Word.assign;
                        } else if (str.compareTo("to") == 0) {
                            str = " ";
                            return Word.to;
                        } else if (str.compareTo("conditional") == 0) {
                            str = " ";
                            return Word.conditional;
                        } else if (str.compareTo("option") == 0) {
                            str = " ";
                            return Word.option;
                        } else if (str.compareTo("do") == 0) {
                            str = " ";
                            return Word.dotok;
                        } else if (str.compareTo("else") == 0) {
                            str = " ";
                            return Word.elsetok;
                        } else if (str.compareTo("while") == 0) {
                            str = " ";
                            return Word.whiletok;
                        } else if (str.compareTo("begin") == 0) {
                            str = " ";
                            return Word.begin;
                        } else if (str.compareTo("end") == 0) {
                            str = " ";
                            return Word.end;
                        } else if (str.compareTo("print") == 0) {
                            str = " ";
                            return Word.print;
                        } else if (str.compareTo("read") == 0) {
                            str = " ";
                            return Word.read;
                            // ... gestire il caso degli identificatori e delle parole chiave FATTO//
                        } else {

                            Word tok = new Word(Tag.ID, str);
                            str = " ";
                            return tok;
                        }
                    } else if (Character.isDigit(peek)) {
                        String str = getNum(br);
                        if (str == null) {
                            System.err.println("Erroneous character: " + peek);
                            return null;
                        }
                        NumberTok tok = new NumberTok(str);
                        str = " ";
                        return tok;

                        // FATTO? ... gestire il caso dei numeri ... //

                    } else {
                        System.err.println("Erroneous character: "
                                + peek);
                        return null;
                    }
            }
        } catch (

        NullPointerException n) {
            System.err.println("Error!");
            return null;
        }

    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\natha\\Desktop\\Nathan\\LFT\\Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}