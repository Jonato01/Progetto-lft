import java.io.*;

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) {
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
            if (look.tag != Tag.EOF)
                move();
        } else {
            System.out.println(look.tag + "   " + t);
            error("syntax error in match");
        }
    }

    public void start() {
        if (look.tag == Tag.EOF) {
            error("Empty file!");
        } else if (look.tag != '(' && look.tag != Tag.NUM) {
            error("Error in start: found ' " + look.tag + " '");
        }

        int expr_val;
        expr_val = expr();
        match(Tag.EOF);
        System.out.println("The value of the expression is: " + expr_val);
    }

    private int expr() {
        if (look.tag != '(' && look.tag != Tag.NUM)
            error("Error in expr: found ' " + look.tag + " '");

        int term_val, exprp_val;
        term_val = term();
        exprp_val = exprp(term_val);
        return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                return exprp_val;
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                return exprp_val;
            case ')', Tag.EOF:
                return exprp_i;
            default:
                error("Error in exprp: found ' " + look.tag + " '");
                return exprp_i;
        }
    }

    private int term() {
        if (look.tag == '(' && look.tag == Tag.NUM)
            error("Error in term: found ' " + look.tag + " '");

        int fact_val, termp_val;
        fact_val = fact();
        termp_val = termp(fact_val);
        return termp_val;
    }

    private int termp(int termp_i) {
        int fact_val, termp_val;
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                return termp_val;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                return termp_val;
            case '+', '-', ')', Tag.EOF:
                return termp_i;
            default:
                error("Error in termp: found ' " + look.tag + " '");
                return termp_i;
        }
    }

    private int fact() {
        int expr_val, fact_val;
        switch (look.tag) {
            case '(':
                match('(');
                expr_val = expr();
                match(')');
                return expr_val;
            case Tag.NUM:
                fact_val = Integer.valueOf(((NumberTok) look).lexeme);
                match(Tag.NUM);
                return fact_val;
            default:
                error("Error in fact: found ' " + look.tag + " '");
                return -1;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\natha\\Desktop\\Nathan\\LFT\\Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * NULL(exprp)
 * NULL(termp)
 * 
 * FIRST(start) = {(, NUM}
 * FIRST(expr) = {(, NUM}
 * FIRST(exprp) = {+, -, eps}
 * FIRST(term) = {(, NUM}
 * FIRST(termp) = {*, /, eps}
 * FIRST(fact) = {(, NUM}
 * 
 * FOLLOW(start) = {EOF}
 * FOLLOW(expr) = {), EOF}
 * FOLLOW(exprp) = {), EOF}
 * FOLLOW(term) = {+, -, ), EOF}
 * FOLLOW(termp) = {+, - ), EOF}
 * FOLLOW(fact) = {*, /, +, -, ), EOF}
 * 
 * GUIDA(start -> expr)= {(,NUM}
 * GUIDA(expr -> term exprp)= {(,NUM}
 * GUIDA(exprp-> + term exprp)= {+}
 * GUIDA(exprp-> - term exprp)= {-}
 * GUIDA(exprp-> ε)= {),$}
 * GUIDA(term -> fact termp)= {(,NUM}
 * GUIDA(termp -> * fact termp)={*}
 * GUIDA(termp -> / fact termp)={ /}
 * GUIDA(termp -> ε)= {+,-,),$}
 * GUIDA(fact -> ( exprp ) )= { ( }
 * GUIDA(fact -> NUM)= { NUM }
 * 
 */