import java.util.*;


public class Parser {


    private final List<Lexer.Token> tokens;
    private int current = 0;
    

    // constructor to set the token instance
    public Parser(List<Lexer.Token> tokens){
        this.tokens = tokens;
    }


    // helper function to get the current token to be parsed to AST
    private Lexer.Token peek(){
        return tokens.get(current);
    }


    // parse the previous token to generate ast
    private Lexer.Token previous(){
        return tokens.get(current - 1);
    }

    // helper function to check if the token received has reached the end
    private boolean isAtEnd(){
        return peek().type() == Lexer.TokenType.EOF;
    }


    // helper function to advance to the next token to parse to AST
    private Lexer.Token advance(){
        if(!isAtEnd()){
            current++;
        }

        return previous();
    }


    // check if the current token matches a type
    private boolean checkTokenType( Lexer.TokenType type){
        if(isAtEnd()){
            return false;
        }

        return peek().type() == type;
    }

    // helper function to check the token matches with the given types
    // if its not matched , return false

    private boolean match(Lexer.TokenType... types){
        for(Lexer.TokenType type : types){

            if(checkTokenType(type)){
                advance();
                return true;
            }
        }

        return false;
    }

    // helper function to consume each token to generate the AST
    private Lexer.Token consume(Lexer.TokenType type , String message){
        if(checkTokenType(type)){
            return advance();
        } 

        throw new RuntimeException("[Line " + peek().line() + "] Parser Error: " + message + " (got '" + peek().lexeme() + "')");
    }
}