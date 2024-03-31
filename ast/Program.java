package ast;

import java.util.List;

import javax.swing.plaf.nimbus.State;

public class Program implements Node{
    private List<Statement> statements;

    public Program(List<Statement> statements){
        this.statements = statements;
    }
    public Program(){}
    @Override
    public String getTokenLiteral() {
        if(statements.size() > 0) {
            return statements.get(0).getTokenLiteral();
        } else {
            return "";
        }
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public void addStatement(Statement statement){
        this.statements.add(statement);
    }

    @Override
    public String string() {
        StringBuilder out = new StringBuilder();
        for(Statement s: statements){
            out.append(s.toString());
        }
        

        return out.toString();
        
    }
    

    
}
