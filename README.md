# Java to Assembly Compiler

A custom compiler written in Java that translates a subset of Java source code into assembly language.

- **Stage 1 (Lexical Analysis / Tokenizer):** Complete & Verified ✅
- **Stage 2 (Syntax Analysis / Parser):** In Progress 🚧

---

## 🚀 Features

### Stage 1: Lexer (Tokenizer)
The lexer converts raw Java-like source code into a stream of categorized, position-aware tokens:
- **Keywords:** `public`, `class`, `static`, `void`, `int`, `boolean`, `String`, `if`, `else`, `while`, `return`, `true`, `false`, `print`
- **Literals:**
  - Integer numbers (`NUMBER`)
  - String literals (`STRING`, e.g., `"Hello world!"`)
- **Identifiers:** Variable names, function names, class names
- **Built-in Recognition:** Maps `System.out.println` directly to a `PRINT` token
- **Operators & Delimiters:**
  - Arithmetic: `+`, `-`, `*`, `/`
  - Comparison & Assignment: `=`, `==`, `!`, `!=`, `<`, `<=`, `>`, `>=`
  - Delimiters: `(`, `)`, `{`, `}`, `[`, `]`, `;`, `,`, `.`
- **Comments & Position Tracking:**
  - Automatically skips single-line comments (`// ...`) and whitespace
  - Accurately tracks `line` and `column` numbers for detailed error reporting

### Stage 2: Parser (AST Construction - In Progress)
- Skeleton and stream navigation helpers implemented (`peek`, `previous`, `advance`, `checkTokenType`, `match`, `consume`).
- Prepares token stream for AST node generation and syntax enforcement.

---

## 📁 Project Structure

```text
example-repo/
├── .github/
│   └── workflows/
│       └── build.yml      # GitHub Actions CI workflow (auto-build & test)
├── .gitignore             # Ignores compiled .class files and temporary build artifacts
├── Lexer.java             # Stage 1: Lexical analyzer (TokenType enum, Token record)
├── Parser.java            # Stage 2: Syntax analyzer (Token stream navigation & AST parser)
├── Main.java              # Test driver executing sample programs through the pipeline
└── README.md              # Project documentation and changelog
```

---

## ⚙️ CI/CD Automation (GitHub Actions)

A GitHub Actions workflow is configured in `.github/workflows/build.yml` to ensure code stability:
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

3. **Run the test driver:**
   ```bash
   java Main
   ```

### Sample Output

Given sample input code:
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

The lexer produces:
```text
PUBLIC         'public' (line 1, col 1)
CLASS          'class' (line 1, col 8)
IDENTIFIER     'Test' (line 1, col 14)
LBRACE         '{' (line 1, col 19)
PUBLIC         'public' (line 2, col 5)
STATIC         'static' (line 2, col 12)
VOID           'void' (line 2, col 19)
IDENTIFIER     'main' (line 2, col 24)
LPAREN         '(' (line 2, col 28)
STRING         'String' (line 2, col 29)
LBRACK         '[' (line 2, col 35)
RBRACK         ']' (line 2, col 36)
IDENTIFIER     'args' (line 2, col 38)
RPAREN         ')' (line 2, col 42)
LBRACE         '{' (line 2, col 44)
INT            'int' (line 3, col 5)
IDENTIFIER     'x' (line 3, col 9)
ASSIGN         '=' (line 3, col 11)
NUMBER         '10' (line 3, col 13)
SEMICOLON      ';' (line 3, col 15)
...
EOF            '' (line 12, col 1)
```

---

## 🗺️ Roadmap

- [x] **Stage 1: Lexer** (Tokenization, keyword mapping, position tracking)
- [ ] **Stage 2: Parser** (In Progress: Token navigation, AST nodes, grammar validation)
- [ ] **Stage 3: Semantic Analysis** (Type checking, symbol table, scope resolution)
- [ ] **Stage 4: Code Generation** (Translation of AST/IR to Assembly)

---

## 📝 Changelog

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
