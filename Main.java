import java.util.ArrayList;
import java.util.List;

  public class Main {
    public static void main(String[] args) {

        
        String mainSource = """
                public class Main {
                    public static void main(String[] args) {
                        int result = MathHelper.add(10, 20);
                        System.out.println("Finished adding!");
                    }
                }
                """;


        String mathSource = """
                 public class MathHelper {
                    public static int add(int a, int b) {
                        return a + b;
                    }
                }
                """;

            System.out.println(mainSource + "\n");
            System.out.println(mathSource);

           
            
            AST.Program program1 = new Parser(new Lexer(mathSource).tokenize()).parse();

            AST.Program program2 = new Parser(new Lexer(mainSource).tokenize()).parse();

            List<AST.ClassDecl> allClasses = new ArrayList<>();
            allClasses.addAll(program1.classes());
            allClasses.addAll(program2.classes());

            AST.Program combinedProgram = new AST.Program(allClasses);

            CodeGenerator codegen = new CodeGenerator();
            String asm = codegen.generate(combinedProgram);

            FileOut fileout = new FileOut();
            fileout.getFileSource(asm);



    }
}

