
import java.util.*;

public class CodeGenerator {
    



    private final StringBuilder textSection = new StringBuilder();
    private final Map<String , String> stringLiterals = new LinkedHashMap<>();
    private final Map<String , Integer> localOffsets = new HashMap<>();


    private int nextStringId = 0;
    private int nextLabelId = 0;
    private int currentStackOffSet = 0;
    private String currentMethodEndLabel = "default string";

    public String generate(AST.Program program){
        textSection.setLength(0);

        stringLiterals.clear();

        nextStringId = 0;
        nextLabelId = 0;

        textSection.append(".intel_syntax noprefix\n");
        textSection.append(".global main\n");
        textSection.append(".extern puts\n\n");
        textSection.append(".section .text\n");

        for(AST.ClassDecl cd : program.classes()){
            generateClass(cd);
        }


        StringBuilder dataSection = new StringBuilder();

        dataSection.append(".section data\n");

        for(Map.Entry<String , String> entry : stringLiterals.entrySet()){
            dataSection.append(String.format(" %s: .asciz \"%s\"\n", entry.getValue() , entry.getKey()));
        }

        return dataSection.toString() + textSection.toString();
    }



    private void generateClass(AST.ClassDecl classDecl){
        for(AST.MethodDecl md : classDecl.methods()){
            generateMethod( classDecl.name(), md);
        }
    }


    // add classname to support multiple files parsing 
    private void generateMethod( String className , AST.MethodDecl methodDecl){
            localOffsets.clear();

            currentStackOffSet = 0;

            String methodName = methodDecl.name();

            // add non main method with classname_methodname
            String labelName = methodName.equals("main") ? "main" : className + "_" + methodName;

            // append to the top of assembly file 
            textSection.append("\n").append(labelName).append(":\n");

            textSection.append("    push rbp\n");
            textSection.append("    mov rbp , rsp\n");
            textSection.append("    sub rsp, 32\n");


            String[] argRegs = {"rcx" , "rdx" , "r8" , "r9"};


            for(int i = 0 ; i < methodDecl.parameters().size() && i < 4 ; i++){
                AST.Parameter param = methodDecl.parameters().get(i);
                currentStackOffSet += 8;
                localOffsets.put(param.name(), currentStackOffSet);
                textSection.append("   mov [rbp - ").append(currentStackOffSet).append("],").append(argRegs[i]).append("\n");
            }

            currentMethodEndLabel = ".L_" + methodName + "_end";


            for(AST.Stmt stmt : methodDecl.body().statements()){
                generateStatements(stmt);
            }


            textSection.append(currentMethodEndLabel).append(":\n");
            if (methodName.equals("main")){
                textSection.append("    mov rax, 0\n");
            }

           textSection.append("    mov rsp, rbp\n");
           textSection.append("    pop rbp\n");
           textSection.append("    ret\n");

    }


    // helper function to generate statements in assembly , 
    // parse from ast nodes for statements flag and generate the expressions for each statements
    private void generateStatements(AST.Stmt stmt){
        if(stmt instanceof AST.Stmt.VarDecl variableDecl){
            currentStackOffSet += 8;

            localOffsets.put(variableDecl.name() , currentStackOffSet);

            // if variable declared has an initializer , generate the expression
            if(variableDecl.initializer() != null){
                generateExpressions(variableDecl.initializer());
                textSection.append("    mov [rbp - ").append(currentStackOffSet).append("], rax\n");
            }
        } else if (stmt instanceof AST.Stmt.Print print){
            if(print.expression() instanceof AST.Expr.StringLiteral stringLiteral){
                String label = stringLiterals.computeIfAbsent( stringLiteral.value() , k -> "msg_" + (nextStringId++));
                textSection.append("      lea rcx, [rip + ").append(label).append("]\n");
                textSection.append("      lea rdi, [rip + ").append(label).append("]\n");
                textSection.append("      call puts\n");
            }
        } else if ( stmt instanceof AST.Stmt.If ifStatements){
            String elseLabel = ".L_else_" + (nextLabelId++);
            String endLabel = ".L_endif_" + (nextLabelId++);


            generateCondition(ifStatements.condition(), elseLabel);
            generateStatements(ifStatements.thenBranch());
            textSection.append("    jmp ").append(endLabel).append("\n");
            textSection.append(elseLabel).append(":\n");
            
            if(ifStatements.elseBranch() != null){
                generateStatements(ifStatements.elseBranch());
            }

            textSection.append(endLabel).append(":\n");

        } else if (stmt instanceof AST.Stmt.Return returnKeyword){

            if(returnKeyword.value() != null){
                generateExpressions(returnKeyword.value());
            }

        } else if (stmt instanceof AST.Stmt.Block block){
            for(AST.Stmt s : block.statements()){
                generateStatements(s);
            }
        } 
    }


    private void generateCondition(AST.Expr condition , String jumpTargetOnFalse){
        if (condition instanceof AST.Expr.Binary binary){
            generateExpressions(binary.left());
            textSection.append("    push rax\n");
            generateExpressions(binary.right());
            textSection.append("    mov rbx, rax\n");
            textSection.append("    pop rax\n");
            textSection.append("    cmp rax, rbx\n");

            switch(binary.operator().type()){
                case LESS_EQUAL:
                   textSection.append("    jg ").append(jumpTargetOnFalse).append("\n");
                   break;

                case LESS:
                   textSection.append("    jge ").append(jumpTargetOnFalse).append("\n");
                   break;

                case GREATER:
                   textSection.append("    jle ").append(jumpTargetOnFalse).append("\n");
                   break;


                case EQUAL:
                   textSection.append("    jne ").append(jumpTargetOnFalse).append("\n");
                   break;

                case GREATER_EQUAL:
                   textSection.append("    jl ").append(jumpTargetOnFalse).append("\n");
                   break;

                default:
                    textSection.append("    je ").append(jumpTargetOnFalse).append("\n");
                    
            }   
        }

    }


    // helper function to generate code for expressions depending on expression flag
    private void generateExpressions(AST.Expr expression){
        if(expression instanceof AST.Expr.NumberLiteral numberLiteral){
            textSection.append("   mov rax, ").append(numberLiteral.value()).append("\n");

        } else if(expression instanceof AST.Expr.Variable variable){

            int offset = localOffsets.get(variable.name());
            textSection.append(" mov rax, [rbp - ").append(offset).append("]\n");

        } else if (expression instanceof AST.Expr.Call call){
               String[] winRegs = { "rcx", "rdx", "r8", "r9" };
               String[] linRegs = { "rdi", "rsi", "rdx", "rcx" };

               for(int i = 0 ; i < call.arguments().size() && i< 4; i++ ){
                generateExpressions(call.arguments().get(i));
                textSection.append("    mov ").append(winRegs[i]).append(", rax\n");
                textSection.append("    mov ").append(linRegs[i]).append(", rax\n");
               }

               String label = (call.className() != null) ? call.className() + "_" + call.methodName() : call.methodName();

            textSection.append("    call ").append(label).append("\n");


        
        } else if (expression instanceof AST.Expr.Binary binary){
            generateExpressions(binary.left());
            textSection.append("    push rax\n");
            generateExpressions(binary.right());
            textSection.append("    mov rbx, rax\n");
            textSection.append("    pop rax\n");

            switch(binary.operator().type()){
                case PLUS:
                    textSection.append("    add rax, rbx\n");
                    break;
                case MINUS:
                    textSection.append("    sub rax, rbx\n");
                    break;
                case MULTIPLY:
                    textSection.append("    mul rbx\n");
                    break;
                case DIVIDE:
                    textSection.append("    div rbx\n");
                    break;
              
                default:
                    textSection.append("    cmp rax, rbx\n");
                    textSection.append("    sete al\n");
                    textSection.append("    movzx rax, al\n");
                    break;
            }
        }
    }


    
}
