import java.io.*;

public class Parser2 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser2(Lexer l, BufferedReader br) {
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

    void prog() {
        statlist();
        match(Tag.EOF);
    }

    void statlist() {
        stat();
        statlistp();
    }

    void statlistp() {
        if (look.tag == ';') {
            match(';');
            stat();
            statlistp();
        }
    }

    void stat() {
        switch (look.tag) {
            case Tag.ASSIGN:
                match(Tag.ASSIGN);
                expr();
                match(Tag.TO);
                idlist();
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('[');
                exprlist();
                match(']');
                break;

            case Tag.READ:
                match(Tag.READ);
                match('[');
                idlist();
                match(']');
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
                break;

            case Tag.COND:
                match(Tag.COND);
                match('[');
                optlist();
                match(']');
                statp();
                break;

            case '{':
                match('{');
                statlist();
                match('}');
                break;

            default:
                break;
        }
    }

    void statp() {
        switch (look.tag) {
            case Tag.END:
                match(Tag.END);
                break;

            case Tag.ELSE:
                match(Tag.ELSE);
                stat();
                match(Tag.END);
                break;

            default:
                break;
        }
    }

    void idlist() {
        match(Tag.ID);
        idlistp();
    }

    void idlistp() {
        if (look.tag == ',') {
            match(',');
            match(Tag.ID);
            idlistp();
        }
    }

    void optlist() {
        optitem();
        optlistp();
    }

    void optlistp() {
        if (look.tag == Tag.OPTION) { // !!!!!!
            optitem();
            optlistp();
        }
    }

    void optitem() {
        match(Tag.OPTION);
        match('(');
        bexpr();
        match(')');
        match(Tag.DO);
        stat();
    }

    void bexpr() {
        match(Tag.RELOP);
        expr();
        expr();
    }

    void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;

            case '-':
                match('-');
                expr();
                expr();
                break;

            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;

            case '/':
                match('/');
                expr();
                expr();
                break;

            case Tag.NUM:
                match(Tag.NUM);
                break;

            case Tag.ID:
                match(Tag.ID);
                break;

            default:
                break;
        }
    }

    void exprlist() {
        expr();
        exprlistp();
    }

    void exprlistp() {
        if (look.tag == ',') {
            match(',');
            expr();
            exprlistp();
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\natha\\Desktop\\Nathan\\LFT\\Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser2 parser = new Parser2(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Calcolare insiemi guida per la gestione degli errori