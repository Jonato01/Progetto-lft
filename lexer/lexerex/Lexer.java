import java.io.*; 
import java.util.*;

import lexerex.Tag;
import lexerex.Word;
import lexerex.Token;
import lexerex.NumberTok;
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

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

	// ... gestire i casi di ( ) [ ] { } + - * / ; , ... //
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

                    }
            case ';':
                peek = ' ';
                return Token.semicolon;

            case ',':
                peek = ' ';
                return Token.comma;
            
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

	// ... gestire i casi di || < > <= >= == <> ... //
            case '|':
                readch(br);
                if (peek == '|') { 
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else {
                    return Word.ge;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    return Word.assign;
                }
            case (char)-1:
                return new Token(Tag.EOF);
            default:
                    if (Character.isLetter(peek)) {
	            // ... gestire il caso degli identificatori e delle parole chiave //
                       
                        String word = "";
                        do {
                            word += peek;
                            readch(br);
                        } while (Character.isLetter(peek) || Character.isDigit(peek) || peek=='_');

                        switch(word.toLowerCase()) {
                            case "begin":
                                return Word.begin;
                            case "end":
                                return Word.end;
                            case "option":
                                return Word.option;
                            case "while":
                                return Word.whiletok;
                            case "do":
                                return Word.dotok;    
                            case "conditional":
                                return Word.conditional;
                            case "else":
                                return Word.elsetok;
                            case "print":
                                return Word.print;
                            case "read":
                                return Word.read;
                            default:    
                            return new Word(Tag.ID, word);

                }  } else if (Character.isDigit(peek)) {
                // ... gestire il caso dei numeri ... //
                int val=0;
                    do{
                        val=val*10+(peek-'0');
                        readch(br);

                    }while(Character.isDigit(peek));
                    return new NumberTok(val);
                    } else {
                        System.err.println("Erroneous character: "+ peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {

        Lexer lex = new Lexer();
        String path = "/home/renatogioana/Documents/java/lexer/lexer_text.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
