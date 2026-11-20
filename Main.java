import java.util.List;

  public class Main {
    public static void main(String[] args) {

        
        String sampleSource = """
                public class Test {
                    public static void main(String[] args) {
                    int x = 10;
                    int y = 20;
                    if (x <= y) {
                        System.out.println("Hello world!");
                    } else {
                        return;
                    }
                }
                }
                """;

            System.out.println(sampleSource);

            Lexer lexer = new Lexer(sampleSource);

            List<Lexer.Token> tokens = lexer.tokenize();

            for (Lexer.Token token : tokens){
                System.out.println(token);
            }

            Parser parser = new Parser(tokens);
            AST.Program program = parser.parse();

            System.out.println("\n=== PARSED AST PROGRAM ===");
            System.out.println(program);
    }
}

