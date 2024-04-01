package repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import ast.Program;
import object.Environment;
import object.Object;
import evaluator.Evaluator;
import lexer.Lexer;
import parser.Parser;
import token.Token;
import token.TokenType;

public class Repl {

    private static final String PROMPT = ">> ";

    public static void Start() throws IOException{

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        Environment env = new Environment();

        for(;;){
            out.print(PROMPT);
            out.flush();

            String line = in.readLine();
            if(line == null){
                break;
            }

            Lexer lexer = new Lexer(line);
            Parser p = new Parser(lexer);
            Program program = new Program();
            try {
                program = p.ParseProgram();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(p.getErrors().size() != 0){
                printParserErrors(out, p.getErrors());
                continue;
            }

            Object evaluated = Evaluator.eval(program, env);
            if(evaluated != null){
                out.println(evaluated.inspect());
            }
            
            out.flush();
            


        }
        in.close();
        out.close();
    }

    private static void printParserErrors(PrintWriter out, List<String> errors){
        for(String msg: errors){
            out.println("\t" + msg);
        }
    }
    
        
    

    
}
