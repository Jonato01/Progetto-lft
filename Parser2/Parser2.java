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
        if (look.tag == Tag.EOF) {
            error("Empty file!");
        } else if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.WHILE
                || look.tag == Tag.COND || look.tag == '{') {
            statlist();
            match(Tag.EOF);
        } else {
            error("Error in prog");
        }
    }

    void statlist() {
        if (look.tag == Tag.ASSIGN || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.WHILE
                || look.tag == Tag.COND || look.tag == '{') {
            stat();
            statlistp();
        } else {
            error("Error in expr");
        }
    }

    void statlistp() {
        if (look.tag == ';') {
            match(';');
            stat();
            statlistp();
        } else if (look.tag == '}' || look.tag == Tag.EOF) {
            return;
        } else {
            error("Error in statlistp");
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
                if (look.tag == '}') {
                    return;
                } else {
                    error("Error in stat");
                }
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
                error("Error in statp");
                break;
        }
    }

    void idlist() {
        if (look.tag == Tag.ID) {
            match(Tag.ID);
            idlistp();
        } else {
            error("Error in idlist");
        }

    }

    void idlistp() {
        if (look.tag == ',') {
            match(',');
            match(Tag.ID);
            idlistp();
        } else if (look.tag == ']' || look.tag == ';' || look.tag == '}' || look.tag == Tag.END) {
            return;
        } else {
            error("Error in idlistp");
        }
    }

    void optlist() {
        if (look.tag == Tag.OPTION) {
            optitem();
            optlistp();
        } else {
            error("error in optlist");
        }

    }

    void optlistp() {
        if (look.tag == Tag.OPTION) {
            optitem();
            optlistp();
        } else if (look.tag == ']') {
            return;
        } else {
            error("Error in optlistp");
        }
    }

    void optitem() {
        if (look.tag == Tag.OPTION) {
            match(Tag.OPTION);
            match('(');
            bexpr();
            match(')');
            match(Tag.DO);
            stat();
        } else {
            error("error in optitem");
        }
    }

    void bexpr() {
        if (look.tag == Tag.RELOP) {
            match(Tag.RELOP);
            expr();
            expr();
        } else {
            error("Error in bexpr");
        }
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
                error("Error in expr");
                break;
        }
    }

    void exprlist() {
        if (look.tag == '+' || look.tag == '-' || look.tag == '*' || look.tag == '/' || look.tag == Tag.NUM
                || look.tag == Tag.ID) {
            expr();
            exprlistp();
        } else {
            error("Error in exprlist");
        }
    }

    void exprlistp() {
        if (look.tag == ',') {
            match(',');
            expr();
            exprlistp();
        } else if (look.tag == ')' || look.tag == ']') {
            return;
        } else {
            error("Error in exprlistp");
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
