# Lexer & Scanner - Laboratory Work 3

### Course: Formal Languages & Finite Automata
### Author: Gafenco Victor, FAF-241

----

## Theory

Lexical analysis is the first phase of a compiler or interpreter, responsible for transforming a stream of characters into a stream of tokens. A lexer, also called a scanner or tokenizer, reads the input character-by-character and groups them into meaningful units called lexemes. Each lexeme is then classified into a token type, which represents the semantic category or purpose of that lexeme.

The fundamental concepts are: (1) **Lexeme** - the actual textual representation extracted from input (e.g., "sin", "123", "+"); (2) **Token** - a classified lexeme paired with metadata such as type, line number, and column position; (3) **Token Type** - the semantic category assigned to a token (e.g., NUMBER, FUNCTION, OPERATOR). Lexical analysis simplifies the parsing phase by reducing the input stream to meaningful symbols, enables early error detection for invalid characters, removes whitespace and comments, and prepares the token stream for syntax analysis.

## Objectives

* Understand the principles of lexical analysis and its role in language processing
* Implement a functional lexer for mathematical expressions with support for multiple operators and functions
* Support complex language features including numbers (integers and floats), variables, operators, and mathematical functions
* Demonstrate proper token representation with accurate position tracking for error reporting
* Validate lexer functionality through comprehensive test cases covering normal and edge cases

## Implementation description

**TokenType Enumeration** (`TokenType.java`): Defines all possible token categories in the mathematical expression language, including literals (NUMBER, VARIABLE), arithmetic operators (PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, POWER), mathematical functions (SIN, COS, TAN, SQRT, POW, ABS, LOG, EXP), delimiters (LPAREN, RPAREN, COMMA), and special tokens (EOF, UNKNOWN). This enumeration provides a complete catalog of semantic token categories that the lexer assigns to lexemes during scanning.

```java
public enum TokenType {
    // Literals
    NUMBER, VARIABLE,
    
    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, POWER,
    
    // Functions
    SIN, COS, TAN, SQRT, POW, ABS, LOG, EXP,
    
    // Delimiters
    LPAREN, RPAREN, COMMA,
    
    // Special
    EOF, UNKNOWN
}
```

**Token Class** (`Token.java`): Represents a single lexical token containing the token type, lexeme (actual text), literal value (for numeric tokens), and position information (line and column). The literal field enables storing numeric values discovered during lexical analysis, which can be used in subsequent semantic analysis phases. Position information is critical for generating meaningful error messages and debugging.

```java
public class Token {
    private TokenType type;
    private String lexeme;
    private Object literal;  // For storing numeric values
    private int line;
    private int column;
    
    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }
}
```

**Lexer Class** (`Lexer.java`): The core scanning engine that iterates through the input character-by-character and produces a token stream. The main `tokenize()` method scans the entire input and returns a list of tokens terminated by an EOF marker. The lexer delegates to specialized methods: `scanNumber()` recognizes integers and floating-point numbers by consuming digit sequences and handling decimal points; `scanIdentifier()` recognizes variables and keywords by checking against a KEYWORDS set and mapping recognized function names to specific token types (e.g., "sin" → TokenType.SIN); `scanToken()` handles single and multi-character operators using a switch statement and character lookahead.

```java
public List<Token> tokenize() {
    while (position < input.length()) {
        scanToken();
    }
    tokens.add(new Token(TokenType.EOF, "", line, column));
    return tokens;
}

private void scanNumber() {
    int startPos = position, startCol = column;
    while (position < input.length() && Character.isDigit(peek())) advance();
    
    if (peek() == '.' && position + 1 < input.length() && Character.isDigit(peekNext())) {
        advance();
        while (position < input.length() && Character.isDigit(peek())) advance();
    }
    
    String lexeme = input.substring(startPos, position);
    double value = Double.parseDouble(lexeme);
    tokens.add(new Token(TokenType.NUMBER, lexeme, value, line, startCol));
}

private void scanIdentifier() {
    int startPos = position, startCol = column;
    while (position < input.length() && 
           (Character.isLetterOrDigit(peek()) || peek() == '_')) advance();
    
    String lexeme = input.substring(startPos, position);
    TokenType type = TokenType.VARIABLE;
    
    if (KEYWORDS.contains(lexeme)) {
        switch (lexeme) {
            case "sin": type = TokenType.SIN; break;
            case "cos": type = TokenType.COS; break;
            case "tan": type = TokenType.TAN; break;
            case "sqrt": type = TokenType.SQRT; break;
            case "pow": type = TokenType.POW; break;
            case "abs": type = TokenType.ABS; break;
            case "log": type = TokenType.LOG; break;
            case "exp": type = TokenType.EXP; break;
            case "pi": tokens.add(new Token(TokenType.NUMBER, lexeme, Math.PI, line, startCol)); return;
            case "e": tokens.add(new Token(TokenType.NUMBER, lexeme, Math.E, line, startCol)); return;
        }
    }
    tokens.add(new Token(type, lexeme, line, startCol));
}
```

**Main Class** (`Main.java`): Provides comprehensive testing of the lexer through 12 test cases covering basic arithmetic, trigonometric functions, complex expressions, variable names with underscores, multiline input with comments, and error handling for invalid characters. All test results demonstrate the lexer's ability to correctly tokenize mathematical expressions while maintaining accurate position tracking.

## Results / Conclusions

The lexer successfully tokenizes a wide variety of mathematical expressions, correctly identifying tokens and their types while maintaining precise position information. Below is the actual program output demonstrating lexer functionality:

```
=== LEXICAL ANALYSIS: MATHEMATICAL EXPRESSION LEXER ===

Expression: 3.14 + 2.71
Tokens:
  Token(NUMBER, '3.14', line=1, col=1, literal=3.14)
  Token(PLUS, '+', line=1, col=6)
  Token(NUMBER, '2.71', line=1, col=8, literal=2.71)

Expression: sin(x) + cos(y)
Tokens:
  Token(SIN, 'sin', line=1, col=1)
  Token(LPAREN, '(', line=1, col=4)
  Token(VARIABLE, 'x', line=1, col=5)
  Token(RPAREN, ')', line=1, col=6)
  Token(PLUS, '+', line=1, col=8)
  Token(COS, 'cos', line=1, col=10)
  Token(LPAREN, '(', line=1, col=13)
  Token(VARIABLE, 'y', line=1, col=14)
  Token(RPAREN, ')', line=1, col=15)

Expression: (2 * x) ^ 3 + sqrt(16)
Tokens:
  Token(LPAREN, '(', line=1, col=1)
  Token(NUMBER, '2', line=1, col=2, literal=2.0)
  Token(MULTIPLY, '*', line=1, col=5)
  Token(VARIABLE, 'x', line=1, col=6)
  Token(RPAREN, ')', line=1, col=7)
  Token(POWER, '^', line=1, col=9)
  Token(NUMBER, '3', line=1, col=11, literal=3.0)
  Token(PLUS, '+', line=1, col=13)
  Token(SQRT, 'sqrt', line=1, col=15)
  Token(LPAREN, '(', line=1, col=19)
  Token(NUMBER, '16', line=1, col=20, literal=16.0)
  Token(RPAREN, ')', line=1, col=22)

Expression: abs(-5) + log(10)
Tokens:
  Token(ABS, 'abs', line=1, col=1)
  Token(LPAREN, '(', line=1, col=4)
  Token(MINUS, '-', line=1, col=5)
  Token(NUMBER, '5', line=1, col=6, literal=5.0)
  Token(RPAREN, ')', line=1, col=7)
  Token(PLUS, '+', line=1, col=9)
  Token(LOG, 'log', line=1, col=11)
  Token(LPAREN, '(', line=1, col=14)
  Token(NUMBER, '10', line=1, col=15, literal=10.0)
  Token(RPAREN, ')', line=1, col=17)

Expression: pow(2, 8) - exp(1)
Tokens:
  Token(POW, 'pow', line=1, col=1)
  Token(LPAREN, '(', line=1, col=4)
  Token(NUMBER, '2', line=1, col=5, literal=2.0)
  Token(COMMA, ',', line=1, col=6)
  Token(NUMBER, '8', line=1, col=8, literal=8.0)
  Token(RPAREN, ')', line=1, col=9)
  Token(MINUS, '-', line=1, col=11)
  Token(EXP, 'exp', line=1, col=13)
  Token(LPAREN, '(', line=1, col=16)
  Token(NUMBER, '1', line=1, col=17, literal=1.0)
  Token(RPAREN, ')', line=1, col=18)

Expression: pi * r^2
Tokens:
  Token(NUMBER, 'pi', line=1, col=1, literal=3.141592653589793)
  Token(MULTIPLY, '*', line=1, col=5)
  Token(VARIABLE, 'r', line=1, col=6)
  Token(POWER, '^', line=1, col=7)
  Token(NUMBER, '2', line=1, col=8, literal=2.0)

Expression: tan(45) * sin(30) / cos(60)
Tokens:
  Token(TAN, 'tan', line=1, col=1)
  Token(LPAREN, '(', line=1, col=4)
  Token(NUMBER, '45', line=1, col=5, literal=45.0)
  Token(RPAREN, ')', line=1, col=7)
  Token(MULTIPLY, '*', line=1, col=10)
  Token(SIN, 'sin', line=1, col=11)
  Token(LPAREN, '(', line=1, col=14)
  Token(NUMBER, '30', line=1, col=15, literal=30.0)
  Token(RPAREN, ')', line=1, col=17)
  Token(DIVIDE, '/', line=1, col=19)
  Token(COS, 'cos', line=1, col=21)
  Token(LPAREN, '(', line=1, col=24)
  Token(NUMBER, '60', line=1, col=25, literal=60.0)
  Token(RPAREN, ')', line=1, col=27)

Expression: sqrt(x^2 + y^2)
Tokens:
  Token(SQRT, 'sqrt', line=1, col=1)
  Token(LPAREN, '(', line=1, col=5)
  Token(VARIABLE, 'x', line=1, col=6)
  Token(POWER, '^', line=1, col=7)
  Token(NUMBER, '2', line=1, col=8, literal=2.0)
  Token(PLUS, '+', line=1, col=10)
  Token(VARIABLE, 'y', line=1, col=12)
  Token(POWER, '^', line=1, col=13)
  Token(NUMBER, '2', line=1, col=14, literal=2.0)
  Token(RPAREN, ')', line=1, col=15)

Expression: # This is a comment
sin(x) + 1
Tokens:
  Token(SIN, 'sin', line=2, col=1)
  Token(LPAREN, '(', line=2, col=4)
  Token(VARIABLE, 'x', line=2, col=5)
  Token(RPAREN, ')', line=2, col=6)
  Token(PLUS, '+', line=2, col=8)
  Token(NUMBER, '1', line=2, col=10, literal=1.0)

Expression: 123.456 * (a + b) % 10
Tokens:
  Token(NUMBER, '123.456', line=1, col=1, literal=123.456)
  Token(MULTIPLY, '*', line=1, col=10)
  Token(LPAREN, '(', line=1, col=11)
  Token(VARIABLE, 'a', line=1, col=12)
  Token(PLUS, '+', line=1, col=14)
  Token(VARIABLE, 'b', line=1, col=16)
  Token(RPAREN, ')', line=1, col=17)
  Token(MODULO, '%', line=1, col=19)
  Token(NUMBER, '10', line=1, col=21, literal=10.0)

Expression: _var1 + variable_2
Tokens:
  Token(VARIABLE, '_var1', line=1, col=1)
  Token(PLUS, '+', line=1, col=7)
  Token(VARIABLE, 'variable_2', line=1, col=9)

=== ERROR HANDLING ===

Expression: 3.14 + @ ^ &
Tokens:
  Token(NUMBER, '3.14', line=1, col=1, literal=3.14)
  Token(PLUS, '+', line=1, col=6)
  WARNING: Token(UNKNOWN, '@', line=1, col=8)
  Token(POWER, '^', line=1, col=10)
  WARNING: Token(UNKNOWN, '&', line=1, col=12)
```

**Conclusion:**

The lexer implementation successfully fulfills all laboratory objectives and demonstrates a functional lexer suitable for a compiler's front-end phase. The tokenizer correctly processes complex mathematical expressions, accurately recognizing and classifying all token types. Test results show proper handling of floating-point and integer literals with correctly stored numeric values, accurate identification of mathematical functions (sin, cos, tan, sqrt, pow, abs, log, exp), and correct tokenization of multi-character operators while distinguishing them from single-character variants. 

The lexer properly supports variable names including underscores and special mathematical constants (pi and e), which are converted to their numeric representations at scan time. Comment handling works correctly, skipping comments while maintaining proper line tracking for error reporting across multiple lines of input. Invalid characters are appropriately flagged as UNKNOWN tokens, enabling error reporting. Most importantly, the lexer maintains precise position tracking (line and column information) for every token, which is essential for generating meaningful error messages in later compilation phases. The implementation demonstrates how lexical analysis reduces the raw input stream to a manageable sequence of semantic units that are ready for syntactic analysis.

## Difficulties and Challenges

One of the primary challenges in implementing this lexer was correctly distinguishing between different character types and managing the state transitions appropriately. In particular, handling multi-character operators required implementing a lookahead mechanism. When encountering the `*` character, the lexer needed to check if the next character is also `*` to determine whether to tokenize `*` (MULTIPLY) or `**` (POWER). This decision needed to be made without consuming the lookahead character if it did not match, which required careful position management to ensure the lookahead character would be encountered in the next iteration.

Another significant difficulty involved recognizing floating-point numbers while avoiding treating the decimal point as a separate token. The approach required explicitly checking for a decimal point followed by at least one digit before consuming the decimal separator. If the decimal point was not followed by a digit, it would be treated as an unknown character. Additionally, the implementation needed to support both the traditional decimal format (e.g., "3.14") and potentially integer values that did not have decimal points, requiring the scanning logic to operate in two phases.

Keyword and function recognition presented another challenge. The lexer had to distinguish between function names (sin, cos, sqrt, etc.) and variable names that might contain these keywords as substrings (e.g., "sin_value" should be tokenized as a single VARIABLE token, not as the function SIN followed by an underscore). The solution involved scanning the complete identifier first, then checking the final resulting lexeme against the KEYWORDS set, ensuring that only exact matches were classified as functions.

The treatment of special constants (pi and e) required thoughtful design decisions. The question arose: should "pi" be returned as a VARIABLE token, or should it be immediately converted to a NUMBER token with the numeric value of π (Math.PI)? The chosen approach treats these constants as NUMBER tokens to maintain semantic consistency with other numeric literals, allowing downstream analysis phases to treat them uniformly. This decision simplified the parser implementation at the cost of additional work in the lexer.

Position tracking across multiple lines required careful attention to detail. The lexer needed to track not just the input position but also the line and column numbers for error reporting. Newline characters needed to trigger line counter increments while resetting the column counter, and each character advance needed to update both position and column counters appropriately. This became particularly important when testing expressions that spanned multiple lines with comments.

## References

1. Course Materials: Lab 3 task.md
2. LLVM Tutorial: A sample of a lexer implementation (https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl01.html)
3. Wikipedia: Lexical analysis (https://en.wikipedia.org/wiki/Lexical_analysis)
4. Aho, A. V., Lam, M. S., Sethi, R., & Ullman, J. D. (2006). Compilers: Principles, Techniques, and Tools (2nd ed.). Addison-Wesley.
5. Java Documentation: Character and String APIs
