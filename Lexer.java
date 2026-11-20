import java.util.*;

import javax.management.RuntimeErrorException;

import java.nio.file.*;


// we need this class for tokenization of each syntax and to check spellings of each syntax 
public class Lexer {

    private String source;
    private List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;
    private int startColumn = 1;

    // constructor to set source variable to an instance 
    public Lexer(String source){
        this.source = source;
    }

    // enum to act as a registry to types of tokens being assigned in lexer 
    public enum TokenType {

        // keywords
        PUBLIC , CLASS , VOID , INT , BOOLEAN , 
        IF , ELSE , WHILE , RETURN , TRUE , FALSE,
        STATIC , STRING, MODULO , NOT,


        // built in , identifiers and numbers
        PRINT , IDENTIFIER , NUMBER,

        // single character symbols
        LPAREN , RPAREN , LBRACE , RBRACE , LBRACK , RBRACK,
        SEMICOLON , COMMA , DOT , SLASH,

        // operators
        PLUS , MINUS , MULTIPLY , DIVIDE,

        // one or two / more operators
        ASSIGN , EQUAL, BANG , NOT_EQUAL , LESS , LESS_EQUAL ,
        GREATER , GREATER_EQUAL , AND , OR ,

        EOF

    }

    


    // record class to log the type of token and exact line and columns in code , 
    // the token is being assigned to
    public record Token(TokenType type , String lexeme , int line , int column){

        @Override
        public String toString(){
            return String.format("%-14s '%s' (line %d, col %d)", type, lexeme, line, column);
        }
    }


    // helper function to scan column and detect and add tokens to be processed to the parser

    private void  scanToken(){
        char c = advance();

        switch (c) {
            case ' ' , '\r' , '\t':
                {}
                break;

            case '\n':
                line++;
                column = 1;
                break;

            case '(':
                addToken(TokenType.LPAREN);
                System.out.println("TOKEN (LPAREN)");
                break;

            case ')':
                addToken(TokenType.RPAREN);
                System.out.println("TOKEN (RPAREN)");
                break;


            case '{':
                addToken(TokenType.LBRACE);
                System.out.println("TOKEN (LBRACE)");
                break;

            case '}':
                addToken(TokenType.RBRACE);
                System.out.println("TOKEN (RBRACE)");
                break;

            case '[':
                addToken(TokenType.LBRACK);
                System.out.println("TOKEN (LBRACK)");
                break;

            case ']':
                addToken(TokenType.RBRACK);
                System.out.println("TOKEN (RBRACK)");
                break;

            case ';':
                addToken(TokenType.SEMICOLON);
                System.out.println("TOKEN (SEMICOLON)");
                break;

            case ',':
                 addToken(TokenType.COMMA);
                 System.out.println("TOKEN (COMMA)");
                 break;

            case '.' :
                 addToken(TokenType.DOT); 
                System.out.println("TOKEN (DOT)");
                break;

            case '+' :
                addToken(TokenType.PLUS);
                System.out.println("TOKEN (PLUS)");
                break;


            case '-' :
                addToken(TokenType.MINUS);
                System.out.println("TOKEN (SUBSTRACT)");
                break;


            case '*' :
                addToken(TokenType.MULTIPLY);
                System.out.println("TOKEN (MULTIPLY)");
                break;

            case '/':

                if (match('/')){
                    while(peek() != '\n'&& !isAtEnd()){
                        advance();
                    }
                } else {
                    addToken(TokenType.DIVIDE);
                    System.out.println("TOKEN (DIVIDE)");
                    
                }
                break;


            case '=':
                addToken(match('=') ? TokenType.EQUAL : TokenType.ASSIGN);
                break;


            case '!':
                addToken(match('=') ? TokenType.NOT_EQUAL : TokenType.BANG);
                break;

            case '"':
                scanString();
                break;
                

            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;

            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            default:
                if(isDigit(c)){
                    scanNumber();
                } else if (isAlpha(c)){
                    scanIdentifiers();
                } else {
                    error("Unexpected Error: Character -> " + "' " + c + " '");
                };
        }

    }




    // add lexer token to each text being scanned from the code
    private void addToken(TokenType type){
        String text = source.substring(start , current);
        tokens.add(new Token(type, text, line, startColumn));
    }


    // helper function to scan one token and move to the next token
    private char advance(){
        column++;
        return source.charAt(current++);
    }

    // returns the current character without consuming it
    private char peek(){
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    // check if the scanner has reached end of file , it will return true or false
    private boolean isAtEnd() {
        return current >= source.length();
    }


    // helper function to match any incoming code with correct token
    private boolean match( char expected){

        // if its at the end index
        // return false
        if(isAtEnd() || source.charAt(current) != expected){
            return false;
        }

        // increment the current and column count to move to the next token

        current++;
        column++;

        // return true to continue moving
        return true;
    }

    // helper function to scan for any number detected from each column
    private void scanNumber(){
        while(isDigit(peek())){
            advance();
        }

        addToken(TokenType.NUMBER);
    }


    // helper function to scan for identifiers in each column position
    private void scanIdentifiers(){

        // keep moving until the end of column position
        while(isAlphaNumeric(peek())){
            advance();
        }

        // if columns detected has print statement , add the print token specifically for this phrase
        if(source.startsWith("System.out.println" , start)){
            while(current < start + "System.out.println".length()){
                advance();
            }

            addToken(TokenType.PRINT);
            return;
        }

        String text = source.substring(start , current);
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    // helper function to scan string selected from each column 
    private void scanString(){
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n'){
                line++;
                column = 1;
            }
            advance();
        }

        if (isAtEnd()){
            error("Unterminated String\n");
            return;
        }

        advance();

        String value = source.substring(start , current);
        tokens.add(new Token(TokenType.STRING, value, line, startColumn));
    }


    // helper function to return numbers
    // this is for numbers such as 1 , 0 , 7 , 9
    private boolean isDigit(char c ){
        return c >= '0' && c <= '9';  
    }

    // helper function to return alphabets
    // this is for identifiers such as letter in each column
    private boolean isAlpha(char c){
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    // helper function to return alphabets and numbers
    private boolean isAlphaNumeric(char c ){
        return isDigit(c) || isAlpha(c);
    }

    // helper function to return error message
    private void error(String message){
        throw new RuntimeException("[Line " + line + ", Col " + startColumn + "] Lexer Error: " + message);
        
    }



    public List<Token> tokenize(){
        while(!isAtEnd()){
            start = current;
            startColumn = column;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

     
    


    // map to map out keywords from java code to related tokens
    // if empty , default to identifier
    public static final Map<String , TokenType> KEYWORDS = Map.ofEntries(
        

        Map.entry("public", TokenType.PUBLIC),
        Map.entry("class" , TokenType.CLASS),
        Map.entry("static",  TokenType.STATIC),
        Map.entry("void",    TokenType.VOID),
        Map.entry("int",     TokenType.INT),
        Map.entry("boolean", TokenType.BOOLEAN),
        Map.entry("if",      TokenType.IF),
        Map.entry("else",    TokenType.ELSE),
        Map.entry("while",   TokenType.WHILE),
        Map.entry("return",  TokenType.RETURN),
        Map.entry("true",    TokenType.TRUE),
        Map.entry("false",   TokenType.FALSE),
        Map.entry("String", TokenType.STRING),
        Map.entry("print", TokenType.PRINT)
    
    );

    // function to lookup identifier 
    // return the scanned token , or default to identifier if empty
    public static TokenType lookupIdentifier(String text){
        return KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
    }

    




    
}