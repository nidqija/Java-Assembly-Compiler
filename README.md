# Java to Assembly Compiler

A custom compiler written in Java that translates a subset of Java source code into x86-64 assembly language.

- **Stage 1 (Lexical Analysis / Tokenizer):** Complete & Verified ✅
- **Stage 2 (Syntax Analysis / Parser & AST):** Complete & Verified ✅
- **Stage 3 (Code Generation / x86-64 Assembly):** Complete & Verified ✅

---

## 🚀 Features

### Stage 1: Lexer (Tokenizer)
The lexer converts raw Java-like source code into a stream of categorized, position-aware tokens:
- **Keywords:** `public`, `class`, `static`, `void`, `int`, `boolean`, `String`, `if`, `else`, `while`, `return`, `true`, `false`, `print`
- **Literals:** Integer numbers (`NUMBER`) and string literals (`STRING`, e.g., `"Hello world!"`)
- **Identifiers:** Variable names, function names, and class names
- **Built-in Recognition:** Maps `System.out.println` directly to a `PRINT` token
- **Operators & Delimiters:** Arithmetic (`+`, `-`, `*`, `/`), comparisons & assignments (`=`, `==`, `!`, `!=`, `<`, `<=`, `>`, `>=`), and punctuation (`(`, `)`, `{`, `}`, `[`, `]`, `;`, `,`, `.`)
- **Position Tracking:** Accurately tracks `line` and `column` numbers for detailed error reporting

### Stage 2: Parser & Abstract Syntax Tree (AST)
A recursive descent parser that validates grammar and constructs an immutable AST using Java 17 sealed interfaces and records:
- **Program & Class Declarations:** Top-level classes, methods with modifiers (`public`, `static`), return types, and parameter lists (`String[] args`, `int x`)
- **Statements:** Variable declarations (`int x = 10;`), conditional branches (`if/else`), loops (`while`), print statements (`System.out.println`), return statements (`return;`), and nested `{ block }` scopes
- **Expressions:** Full operator precedence ladder (assignment, logical OR/AND, equality, comparisons, terms, factors, unary expressions, literals, and parenthesized grouping)

### Stage 3: Code Generation (x86-64 Intel Syntax)
Translates the AST into human-readable, executable x86-64 assembly:
- **Stack Frame Management:** Sets up standard function prologues (`push rbp; mov rbp, rsp`) and aligned stack frames (`sub rsp, 32`)
- **Local Variable Offsets:** Assigns variables dedicated 8-byte stack offsets (`[rbp - 8]`, `[rbp - 16]`)
- **Conditionals & Branching:** Generates comparison instructions (`cmp rax, rbx`) and inverted conditional jumps (`jg`, `jge`, `jl`, `jle`, `je`, `jne`) targeting unique labels
- **Data Section & String Table:** Collects all string constants into `.section data` (`msg_0: .asciz "Hello world!"`) and issues RIP-relative `lea` calls to C runtime `puts`
- **File Output:** Automatically writes the generated assembly to `assembly_code.asm` via `FileOut.java`

---

## 📁 Project Structure

```text
example-repo/
├── .github/
│   └── workflows/
│       └── build.yml      # GitHub Actions CI workflow (auto-build & test)
├── .gitignore             # Ignores compiled .class files and temporary build artifacts
├── AST.java               # Stage 2: AST node hierarchy (Program, ClassDecl, Stmt, Expr)
├── CodeGenerator.java     # Stage 3: AST to x86-64 Intel-syntax assembly emitter
├── FileOut.java           # Writes generated assembly code to disk (assembly_code.asm)
├── Lexer.java             # Stage 1: Lexical analyzer (TokenType enum, Token record)
├── Parser.java            # Stage 2: Recursive descent parser constructing the AST
├── Main.java              # Test driver executing the complete compiler pipeline
├── clean.bat              # Windows batch script to remove *.class build artifacts
├── clean.ps1              # PowerShell script to remove *.class build artifacts
├── assembly_code.asm      # Emitted x86-64 assembly output
└── README.md              # Project documentation and changelog
```

---

## ⚙️ CI/CD Automation (GitHub Actions)

A GitHub Actions workflow is configured in `.github/workflows/build.yml` to ensure continuous compilation stability:
- **Triggers:** Automatically compiles and tests on every `push` and `pull_request` to `master` and `main`.
- **Manual Trigger:** Includes `workflow_dispatch` enabling on-demand manual runs directly from the GitHub Actions tab.
- **Environment:** Runs on `ubuntu-latest` with Eclipse Temurin JDK 17.

---

## 🛠️ Getting Started

### Prerequisites
- **Java Development Kit (JDK 17 or higher)**

### Building and Running Locally

1. **Navigate to the repository folder:**
   ```bash
   cd example-repo
   ```

2. **Compile all Java source files:**
   ```bash
   javac *.java
   ```

3. **Run the full compiler pipeline:**
   ```bash
   java Main
   ```

4. **Clean up compiled `.class` files anytime:**
   ```cmd
   .\clean.bat
   ```

---

## 📊 End-to-End Pipeline Output

### 1. Input Java Source Code
```java
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
```

### 2. Parsed AST Program
```text
Program[classes=[ClassDecl[name=Test, methods=[MethodDecl[isStatic=true, returnType=void, name=main, parameters=[Parameter[type=String[], name=args]], body=Block[statements=[VarDecl[type=int, name=x, initializer=NumberLiteral[value=10]], VarDecl[type=int, name=y, initializer=NumberLiteral[value=20]], If[condition=Binary[left=Variable[name=x], operator=<=, right=Variable[name=y]], thenBranch=Block[statements=[Print[expression=StringLiteral[value=Hello world!]]]], elseBranch=Block[statements=[Return[value=null]]]]]]]]]]]
```

### 3. Generated x86-64 Assembly (`assembly_code.asm`)
```nasm
.section data
 msg_0: .asciz "Hello world!"
.intel_syntax noprefix
.global main
.extern puts

.section .text

main:
    push rbp
    mov rbp , rsp
    sub rsp, 32
   mov rax, 10
    mov [rbp - 8], rax
   mov rax, 20
    mov [rbp - 16], rax
 mov rax, [rbp - 8]
    push rax
 mov rax, [rbp - 16]
    mov rbx, rax
    pop rax
    cmp rax, rbx
    jg .L_else_0
      lea rcx, [rip + msg_0]
      lea rdi, [rip + msg_0]
      call puts
    jmp .L_endif_1
.L_else_0:
    jmp .L_main_end
.L_endif_1:
.L_main_end:
    mov rax, 0
    mov rsp, rbp
    pop rbp
    ret
```

---

## 🗺️ Roadmap

- [x] **Stage 1: Lexer** (Tokenization, keyword mapping, position tracking)
- [x] **Stage 2: Parser & AST** (Recursive descent parsing, precedence ladder, AST records)
- [x] **Stage 3: Code Generation** (x86-64 Intel assembly, stack frames, conditionals, and file export)
- [ ] **Stage 4: Semantic Analysis** (Type checking, symbol table, scope resolution)
- [ ] **Stage 5: Direct Binary Assembly** (Assembling and linking directly with GCC / MinGW)

---

## 📝 Changelog

- **v0.4.0** - *Code Generation to x86-64 Assembly & File Export*
  - Added `CodeGenerator.java` translating AST structures into x86-64 Intel-syntax assembly.
  - Implemented 16-byte aligned stack allocation and 8-byte local variable stack indexing (`[rbp - offset]`).
  - Implemented conditional evaluation and branching (`cmp rax, rbx` and conditional jumps).
  - Added `.section data` string table registration and RIP-relative `lea` addressing for `puts`.
  - Added `FileOut.java` to export the generated assembly directly to `assembly_code.asm`.
  - Added `clean.bat` and `clean.ps1` helper scripts for automated build cleanup.

- **v0.3.0** - *AST Definitions & Expression/Statement Parser*
  - Added `AST.java` modeling complete node hierarchies (Program, ClassDecl, MethodDecl, Parameter, Stmt, Expr) using Java 17 sealed interfaces and immutable records.
  - Implemented full expression precedence parser in `Parser.java` (assignment, logical OR/AND, equality, comparison, addition/subtraction, multiplication/division, unary, literals/grouping).
  - Implemented statement parsing in `Parser.java` (`if/else`, `while`, `print`, `return`, variable declarations, and nested `{ block }` statements).

- **v0.2.0** - *Parser Foundation & CI/CD*
  - Added `Parser.java` scaffold with token stream navigation (`peek`, `advance`, `match`, `consume`).
  - Added GitHub Actions CI workflow (`.github/workflows/build.yml`) with automated triggers and manual `workflow_dispatch`.
  - Added `.gitignore` to prevent tracking compiled `.class` files.
  - Updated documentation to reflect compiler architecture and stages.

- **v0.1.0** - *Stage 1: Lexer Implementation*
  - Added `TokenType` registry covering keywords, literals, operators, and delimiters.
  - Implemented immutable `Token` record with line and column position tracking.
  - Implemented core character scanner (`advance`, `peek`, `match`, `isAtEnd`).
  - Added support for integer literals and string literals (`"..."`).
  - Added single-line comment skipping (`// ...`).
  - Added fast keyword lookup table via `KEYWORDS` map.
  - Added built-in recognition for `System.out.println`.
  - Added test runner in `Main.java` with sample Java source code.
