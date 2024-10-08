import java.io.*;
package lexerex;
import lexerex.Tag;
import lexerex.Word;
import lexerex.Token;
import lexerex.NumberTok;
import lexerex.Lexer;
public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void start() {
	expr();
	match(Tag.EOF);
    }

    private void expr() {
        if(look.tag==256 || look.tag==40)
        {
            term();
            if(look.tag == Token.plus.tag || look.tag == Token.minus.tag || look.tag == ')' || look.tag == Tag.EOF)
                exprp();
            else error("Er expr miss + or - ");    
        }
        else error("Er expr miss Num or (");
        
    }

    private void exprp() {
	    switch (look.tag) {
	        case '+':
            case '-':
                move();
                term();
                exprp();
                break;
	}
    }

    private void term() {
        if(look.tag == 256 || look.tag == 40){
            fact();
            if(look.tag == Token.mult.tag || look.tag == Token.div.tag || look.tag == Token.plus.tag || look.tag == Token.minus.tag || look.tag == ')' || look.tag == Tag.EOF){
                termp();
            }
            else error("Er TERM Miss * or / or + or - or 'EXPR'");
        }
        else error("Er TERM Miss ( or Num");

    }

    private void termp() {
       switch(look.tag){
            case '*':
            case '/':
            move();
            fact();
            termp();
            break;


       }
    }

    private void fact() {
        if(look.tag==256)
        move();
        else 
        if(look.tag != '(')
        error("Er fact Miss ( or Num"); 
        move();
        expr();    
        if(look.tag != ')')
        error("Er fact Miss ( ");
        move();
    }
		
    public static void main(String[] args) {
       Lexer lex = new Lexer();
        String path = "/home/renatogioana/Documents/java/lexer/lexer_text.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
    
}