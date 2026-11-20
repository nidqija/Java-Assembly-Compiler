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


    // check if the current token matches a type from the lexer 

    private boolean checkTokenType( Lexer.TokenType type){
        if(isAtEnd()){
            return false;
        }

        return peek().type() == type;
    }

    // helper function to check the token matches with the given types
    // if its not matched , return false
    // from the generated token , loop through all generated tokens and check if 
    private boolean match(Lexer.TokenType... types){
        for(Lexer.TokenType type : types){

            if(checkTokenType(type)){
                advance();
                return true;
            }
        }

        return false;
    }

    // helper function to consume each token to generate the AST node
    // if the detected token is not the same as what we preset , 
    // we will throw an error using runtime exception, to proceed the program still
    private Lexer.Token consume(Lexer.TokenType type , String message){
        if(checkTokenType(type)){
            return advance();
        } 

        throw new RuntimeException("[Line " + peek().line() + "] Parser Error: " + message + " (got '" + peek().lexeme() + "')");
    }


    // function to call code expressions , such as assignments
    public AST.Expr expression(){
        return assignment();
    }

    // function to call assignemnt token to be made as ast node
    public AST.Expr assignment(){
        AST.Expr expr = logicOr();


        // check if the token detected is an assign token ( = )
        if(match(Lexer.TokenType.ASSIGN)){
            Lexer.Token equals = previous();
            AST.Expr value = assignment();


             if (expr instanceof AST.Expr.Variable var){
                 return new AST.Expr.Assign(var.name() , value);
            }
            
            throw new RuntimeException("[Line " + equals.line() + "] Invalid assignment target.");
        }

        return expr;



    }


    // generating ast nodes for or
    private AST.Expr logicOr(){
        AST.Expr expr= logicAnd();


        while (match(Lexer.TokenType.OR)){
            Lexer.Token operator = previous();
            AST.Expr right = logicAnd();
            expr = new AST.Expr.Binary(expr , operator , right);
        }

        return expr;
    }


    // generating ast nodes for and
    private AST.Expr logicAnd(){
        AST.Expr expr = equality();

        while(match(Lexer.TokenType.AND)){
            Lexer.Token operator = previous();
            AST.Expr right = equality();
            expr = new AST.Expr.Binary(expr , operator , right);
        }

        return expr;
    }

    // generating ast nodes for equality ==
    private AST.Expr equality(){
        AST.Expr expr = comparison();

        while(match(Lexer.TokenType.EQUAL , Lexer.TokenType.NOT_EQUAL)){
            Lexer.Token operator = previous();
            AST.Expr right = comparison();
            expr = new AST.Expr.Binary(expr , operator , right);
        }

        return expr;
    }

    // generating ast nodes for comparison < , > , <= , >=
    private AST.Expr comparison(){
        AST.Expr expr = term();

        while(match(Lexer.TokenType.LESS , Lexer.TokenType.LESS_EQUAL, Lexer.TokenType.GREATER , Lexer.TokenType.GREATER_EQUAL )){
            Lexer.Token operator = previous();
            AST.Expr right = term();
            expr = new AST.Expr.Binary(expr , operator , right);
            };


        return expr;
        }
    


        // ast parsing for addition and substraction
        // we follow pemdas order , which we call factor first 
        private AST.Expr term(){
            AST.Expr expr = factor();

            while(match(Lexer.TokenType.PLUS , Lexer.TokenType.MINUS)){
                Lexer.Token operator = previous();
                AST.Expr right = factor();

                
                expr = new AST.Expr.Binary(expr , operator , right);
            }

            return expr;
        }

        // ast parsing for mutiplication and substraction
        // in this case , we check if there is unary expression first
        //then we check if the parsed token is multiplcation or divide
        // we call it to binary function 
        private AST.Expr factor(){
            AST.Expr expr = unary();

            while(match(Lexer.TokenType.MULTIPLY , Lexer.TokenType.DIVIDE)){
                Lexer.Token operator = previous();
                AST.Expr right = unary();
                expr = new AST.Expr.Binary(expr , operator , right);
            }

            return expr;
        }


        private AST.Expr unary(){
            if(match(Lexer.TokenType.BANG , Lexer.TokenType.MINUS)){
                Lexer.Token operator = previous();
                AST.Expr right = unary();

                return new AST.Expr.Unary(operator , right);
            }

            return primary();
        }


        // helper function to detect any reserved keys such as booleans , string , identifier , number , and number
        private AST.Expr primary(){
            if(match(Lexer.TokenType.FALSE)){
                return new AST.Expr.BooleanLiteral(false);
            }

            if(match(Lexer.TokenType.TRUE)){
                return new AST.Expr.BooleanLiteral(true);
            }

            if(match(Lexer.TokenType.STRING)){

                String raw = previous().lexeme();

                String value = (raw.startsWith("\"") && raw.endsWith("\"") && raw.length() >= 2) ? raw.substring(1 , raw.length() - 1) : raw;

                return new AST.Expr.StringLiteral(value);

            }

            if (match(Lexer.TokenType.IDENTIFIER)){
                return new AST.Expr.Variable(previous().lexeme());
            }

            if(match(Lexer.TokenType.LPAREN)){
                AST.Expr expr = expression();
                consume(Lexer.TokenType.RPAREN, "Expect ')' after expression.");
                return new AST.Expr.Grouping(expr);
            }

            if(match(Lexer.TokenType.NUMBER)){
                int value = Integer.parseInt(previous().lexeme());
                return new AST.Expr.NumberLiteral(value);
            }

            throw new RuntimeException("[Line " + peek().line() + "] Expect expression, got '" + peek().lexeme() + "'");
        }



        // helper function to analyze the if statements from the generated token
     
        private AST.Stmt ifStatements(){

                     // from here , we will consume LPAREN token , if not exists
                     // throw runtime exception error
                     
                    consume(Lexer.TokenType.LPAREN, "Expect '(' after 'if'.");

                    // from here , we will check the condition of the if statement and run the expression function
                    AST.Expr condition = expression();

                    consume(Lexer.TokenType.RPAREN, "Expect ')' after if condition.");


                    AST.Stmt thenBranch = statement();
                    AST.Stmt elseBranch = null;

                    if(match(Lexer.TokenType.ELSE)){
                        elseBranch = statement();
                    }

                    return new AST.Stmt.If(condition , thenBranch , elseBranch);
        }


        private AST.Stmt whileStatement(){
            consume(Lexer.TokenType.LPAREN,"Expect '(' after print." );
            AST.Expr condition = expression();

            consume(Lexer.TokenType.RPAREN ,"Expect ')' after print.");


            AST.Stmt body = statement();

            return new AST.Stmt.While(condition, body);
        }


        private AST.Stmt returnStatement(){
            AST.Expr value = null;

            if(!checkTokenType(Lexer.TokenType.SEMICOLON)){
                value = expression();
            }

            consume(Lexer.TokenType.SEMICOLON , "Expected ';' after return statement");

            return new AST.Stmt.Return(value);
        }

        private AST.Stmt varDeclaration(){
            String type = previous().lexeme();
            Lexer.Token name = consume(Lexer.TokenType.IDENTIFIER , "Expect variable name");
            AST.Expr initializExpr = null;

            if(match(Lexer.TokenType.ASSIGN)){
                initializExpr= expression();
            }

            consume(Lexer.TokenType.SEMICOLON, "Expect ';' after variable declaration.");

            return new AST.Stmt.VarDecl(type , name.lexeme() , initializExpr);
        }

        private List <AST.Stmt> block(){
            List<AST.Stmt> statements = new ArrayList<>();

            while(!checkTokenType(Lexer.TokenType.RBRACE) && !isAtEnd()){
                statements.add(statement());
            }

            consume(Lexer.TokenType.RBRACE , "Expected '}' after block");
            return statements;
        }

        private AST.Stmt printStatement(){
            consume(Lexer.TokenType.LPAREN, "Expect '(' after print.");
            AST.Expr value = expression();
            consume(Lexer.TokenType.RPAREN, "Expect ')' after print argument.");
            consume(Lexer.TokenType.SEMICOLON, "Expect ';' after print statement.");

            return new AST.Stmt.Print(value);
        }

        private AST.Stmt expressionStatement(){
            AST.Expr expr = expression();

            consume(Lexer.TokenType.SEMICOLON , "Expect ';' after expression");
            return new AST.Stmt.ExpressionStmt(expr);
        }








        // statement parsing
        // this function will assist in detecting any parsed tokens and dispatch them to the correct functions
        
        private AST.Stmt statement(){

            // if the received token matched with the if token type
            // parse the token received to the if statement function
            if(match(Lexer.TokenType.IF)){
                return ifStatements();
            }


            if(match(Lexer.TokenType.WHILE)){
                return whileStatement();
            }

            if(match(Lexer.TokenType.PRINT)){
                return printStatement();
            }

            if(match(Lexer.TokenType.RETURN)){
                return returnStatement();
            }

            if (match(Lexer.TokenType.LBRACE)){
                return new AST.Stmt.Block(block());
            }

            if(match(Lexer.TokenType.INT , Lexer.TokenType.BOOLEAN , Lexer.TokenType.STRING)){
                return varDeclaration();
            }


            return expressionStatement();
        }


        // main function for parsing class AST structure
        public AST.Program parse(){
            List<AST.ClassDecl> classes = new ArrayList<>();

            while(!isAtEnd()){
                classes.add(classDeclaration());
            }

            return new AST.Program(classes);
        }


        private AST.ClassDecl classDeclaration(){
            match(Lexer.TokenType.PUBLIC);
            consume(Lexer.TokenType.CLASS, "Expect 'class' keyword");

            Lexer.Token name = consume(Lexer.TokenType.IDENTIFIER, "Expected class name");

            consume(Lexer.TokenType.LBRACE , "Expect '{' before class body");

            List<AST.MethodDecl> methods = new ArrayList<>();

            while(!checkTokenType(Lexer.TokenType.RBRACE) && !isAtEnd()){
                methods.add(methodDeclaration());
            }


            consume(Lexer.TokenType.RBRACE , "Expect '}' after class body");
            return new AST.ClassDecl(name.lexeme() , methods);
            
        }        // helper function to parse method 


        // helper function to do method declaration , 
        // checked if the generated lexer token matches with predefined public token 
        // if the match function returns true , check if there is static token matched with the parsed lexer token
        private AST.MethodDecl methodDeclaration(){
            match(Lexer.TokenType.PUBLIC);

            boolean isStatic = match(Lexer.TokenType.STATIC);

            if(!match(Lexer.TokenType.VOID , 
                Lexer.TokenType.INT,
                Lexer.TokenType.BOOLEAN,
                Lexer.TokenType.STRING
            )){
                consume(Lexer.TokenType.IDENTIFIER, "expect return type");
            }
            String returnType = previous().lexeme();

            Lexer.Token name = consume(Lexer.TokenType.IDENTIFIER , "expected method name");

            consume(Lexer.TokenType.LPAREN, "Expected '(' after method name");

            List<AST.Parameter> parameters = new ArrayList<>();

            if(!checkTokenType(Lexer.TokenType.RPAREN)){
                do {
                    StringBuilder paramType = new StringBuilder();

                    if (match(Lexer.TokenType.INT , Lexer.TokenType.BOOLEAN , Lexer.TokenType.STRING)){
                        paramType.append(previous().lexeme());
                    } else {
                        paramType.append(consume(Lexer.TokenType.IDENTIFIER, "Expect parameter type. ").lexeme());
                    }

                    if (match(Lexer.TokenType.LBRACK)){
                        consume(Lexer.TokenType.RBRACK,"Expect ']' after '[' in parameter type." );
                        paramType.append("[]");
                    }


                    Lexer.Token paramName = consume(Lexer.TokenType.IDENTIFIER, "Expect parameter name");

                    parameters.add(new AST.Parameter(paramType.toString() , paramName.lexeme()));
                } while (match(Lexer.TokenType.COMMA));
            }

            consume(Lexer.TokenType.RPAREN , "Expected ')' after parameters");
            consume(Lexer.TokenType.LBRACE, "Expected '{' after method body");

            AST.Stmt.Block body = new AST.Stmt.Block(block());

            return new AST.MethodDecl(isStatic, returnType, name.lexeme(), parameters, body);
        }




    }

