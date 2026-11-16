# Java to Assembly Compiler

A custom compiler written in Java that translates a subset of Java source code into assembly language. 

Currently, **Stage 1 (Lexical Analysis / Tokenizer)** is fully implemented and tested.

---

## 🚀 Features (Stage 1: Lexer)

The lexer scans raw Java-like source code and produces a sequence of categorized, position-aware tokens:

- **Keywords:** `public`, `class`, `static`, `void`, `int`, `boolean`, `String`, `if`, `else`, `while`, `return`, `true`, `false`, `print`
- **Literals:**
  - Integer numbers (`NUMBER`)
  - String literals (`STRING`, e.g., `"Hello world!"`)
- **Identifiers:** Variable names, function names, class names
- **Built-in Handling:** Recognizes `System.out.println` as a `PRINT` token
- **Operators & Delimiters:**
  - Arithmetic: `+`, `-`, `*`, `/`
  - Comparison & Assignment: `=`, `==`, `!`, `!=`, `<`, `<=`, `>`, `>=`
  - Punctuation: `(`, `)`, `{`, `}`, `[`, `]`, `;`, `,`, `.`
- **Comments & Whitespace:**
  - Single-line comments (`// ...`) are skipped automatically
  - Accurate line and column position tracking across all tokens
- **Error Reporting:**
  - Detects unterminated strings and illegal/unexpected characters with line and column numbers

---

## 📁 Project Structure

```text
Java_Compiler_Assembly/
├── example-repo/
│   ├── Lexer.java        # Core Lexer implementation, TokenType enum, and Token record
│   ├── Main.java         # Driver program to test tokenization on sample source code
│   └── README.md         # Project documentation
```

---

## 🛠️ Getting Started

### Prerequisites
- **Java Development Kit (JDK 17 or higher)**

### Building and Running

1. **Navigate to the repository folder:**
   ```bash
   cd example-repo
   ```

2. **Compile the sources:**
   ```bash
   javac Lexer.java Main.java
   ```

3. **Run the driver:**
   ```bash
   java Main
   ```

### Sample Output

Given the input:
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
- [ ] **Stage 2: Parser** (Abstract Syntax Tree / AST generation, grammar validation)
- [ ] **Stage 3: Semantic Analysis** (Type checking, symbol table, scope resolution)
- [ ] **Stage 4: Code Generation** (Translation of AST/IR to Assembly)

---

## 📝 Changelog

- **v0.1.0** - *Stage 1: Lexer Implementation*
  - Added `TokenType` registry covering keywords, literals, operators, and delimiters.
  - Implemented immutable `Token` record with line and column position tracking.
  - Implemented core character scanner (`advance`, `peek`, `match`, `isAtEnd`).
  - Added support for integer literals and string literals (`"..."`).
  - Added single-line comment skipping (`// ...`).
  - Added fast keyword lookup table via `KEYWORDS` map.
  - Added built-in recognition for `System.out.println`.
  - Added test runner in `Main.java` with sample Java source code.

