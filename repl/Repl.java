package repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import lexer.Lexer;
import token.Token;
import token.TokenType;

public class Repl {

    private static final String PROMPT = ">> ";

    public static void Start() throws IOException{

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);

        for(;;){
            out.print(PROMPT);
            out.flush();

            String line = in.readLine();
            if(line == null){
                break;
            }

            Lexer lexer = new Lexer(line);
            Token tok;

            do {
                tok = lexer.nextToken();
                if (tok != null) {
                    out.println(tok);
                }
            }while(tok != null && tok.getTokenType() != TokenType.EOF);

            out.flush();
            


        }
        in.close();
        out.close();
    }
        
    

    
}
