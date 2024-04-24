package repl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import ast.Program;
import evaluator.Evaluator;
import lexer.Lexer;
import object.Environment;
import parser.Parser;

public class Reader {
    
    public static void Start() throws IOException{
        FileReader fileReader = new FileReader("/home/freezing/schoolProjects/interpreter2/Main.code");
        BufferedReader in = new BufferedReader(fileReader);
        PrintWriter out = new PrintWriter(System.out);
        Environment env = new Environment();
        StringBuilder lines = new StringBuilder();
        String line ;
        while((line = in.readLine()) != null){
            if(line.isBlank()){
                continue;
            }
            lines.append(line);
            if(!line.isBlank()){
                lines.append("\n");
            }
    
        }

        
        Lexer lexer = new Lexer(lines.toString());
        Parser p = new Parser(lexer);
        Program program = new Program();
        try{
            program = p.ParseProgram();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(p.getErrors().size() != 0) {
            printParserErrors(out, p.getErrors());

        }else{
            System.out.println("No error");
        }

       
        System.out.println("");
        Evaluator.eval(program, env);
        
        
        
        in.close();
        out.close();
    }
     private static void printParserErrors(PrintWriter out, List<String> errors){
        for(String msg: errors){
            out.println("\t" + msg);
        }
    }
    
}
