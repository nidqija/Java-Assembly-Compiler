import java.util.*;


public class AST {
    
    // base interface to generate the ast nodes
    public interface Node{}


    // Node interface-implemented derived record class for the main program
    public record Program(List<ClassDecl>classes) implements  Node{}



    // Node interface-implemented record class for class declaration ( class Test )
    public record ClassDecl(String name , List<MethodDecl> methods) implements Node{}

    // node interface-implemented record class for parameter declaration (String[] args , int x)
    public record Parameter(String type , String name) implements Node{}


    // Node interface-implemented derived records class for method declaration
    public record MethodDecl(
        boolean isStatic,
        String returnType,
        String name,
        List<Parameter> parameters,
        Stmt.Block body
    ) implements Node{}


    // public interface inherited from node interface for generating statement nodes
    public sealed interface Stmt extends Node {

       
        // record class to detect block of statements 
        record Block(List<Stmt> statements) implements Stmt{}

        // record class to detect variable declarations
        record VarDecl(String type , String name , Expr initializer) implements Stmt{}

        record If(Expr condition , Stmt thenBranch , Stmt elseBranch) implements Stmt{}

        record While(Expr condition , Stmt body) implements Stmt{}

        record Print(Expr expression) implements Stmt{}

        record Return(Expr value) implements Stmt{}

        record ExpressionStmt(Expr expression) implements Stmt{}
    }

    // public interface inherited from node interface to generate expression nodes 
    public sealed interface Expr extends Node{
        record Binary(Expr left , Lexer.Token operator , Expr right) implements Expr{}

        record Unary (Lexer.Token operator , Expr right) implements Expr{}

        record Assign(String name , Expr value) implements Expr{}

        record Variable(String name) implements Expr{}

        record NumberLiteral(int value) implements Expr{}

        record StringLiteral(String value) implements Expr{}

        record BooleanLiteral(boolean value) implements Expr{}

        record Grouping(Expr expr) implements Expr{}

        record Call(String className , String methodName , List<Expr>arguments) implements Expr{}

    }
}
