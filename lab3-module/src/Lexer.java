import java.util.*;

/**
 * Mathematical Expression Lexer
 * Tokenizes mathematical expressions containing:
 * - Numbers (integers and floats)
 * - Variables (x, y, pi, e, etc.)
 * - Operators (+, -, *, /, %, ^)
 * - Functions (sin, cos, tan, sqrt, pow, abs, log, exp)
 * - Parentheses and delimiters
 */
public class Lexer {
    private String input;
    private int position;      // Current position in input
    private int line;          // Current line number
    private int column;        // Current column number
    private List<Token> tokens;
    
    // Keywords/function names
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "sin", "cos", "tan", "sqrt", "pow", "abs", "log", "exp",
            "pi", "e"
    ));

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
    }

    /**
     * Tokenize the entire input string
     * @return List of tokens
     */
    public List<Token> tokenize() {
        while (position < input.length()) {
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    /**
     * Scan a single token from the current position
     */
    private void scanToken() {
        char c = peek();

        // Skip whitespace (except newlines for tracking line numbers)
        if (c == ' ' || c == '\t' || c == '\r') {
            advance();
            return;
        }

        // Comments (# for line comments)
        if (c == '#') {
            skipLineComment();
            return;
        }

        // Newline
        if (c == '\n') {
            advance();
            line++;
            column = 1;
            return;
        }

        // Numbers (including floats with .)
        if (Character.isDigit(c)) {
            scanNumber();
            return;
        }

        // Identifiers (variables and keywords)
        if (Character.isLetter(c) || c == '_') {
            scanIdentifier();
            return;
        }

        // Single character tokens
        switch (c) {
            case '+':
                addToken(TokenType.PLUS, "+");
                advance();
                break;
            case '-':
                addToken(TokenType.MINUS, "-");
                advance();
                break;
            case '*':
                advance();
                if (peek() == '*') {
                    advance();
                    addToken(TokenType.POWER, "**");
                } else {
                    addToken(TokenType.MULTIPLY, "*");
                }
                break;
            case '/':
                addToken(TokenType.DIVIDE, "/");
                advance();
                break;
            case '%':
                addToken(TokenType.MODULO, "%");
                advance();
                break;
            case '^':
                addToken(TokenType.POWER, "^");
                advance();
                break;
            case '(':
                addToken(TokenType.LPAREN, "(");
                advance();
                break;
            case ')':
                addToken(TokenType.RPAREN, ")");
                advance();
                break;
            case ',':
                addToken(TokenType.COMMA, ",");
                advance();
                break;
            default:
                addToken(TokenType.UNKNOWN, String.valueOf(c));
                advance();
        }
    }

    /**
     * Scan a number token (integer or floating point)
     */
    private void scanNumber() {
        int startPos = position;
        int startCol = column;

        // Consume digits
        while (position < input.length() && Character.isDigit(peek())) {
            advance();
        }

        // Check for decimal point
        if (peek() == '.' && position + 1 < input.length() && Character.isDigit(peekNext())) {
            advance(); // consume '.'
            while (position < input.length() && Character.isDigit(peek())) {
                advance();
            }
        }

        String lexeme = input.substring(startPos, position);
        double value = Double.parseDouble(lexeme);

        tokens.add(new Token(TokenType.NUMBER, lexeme, value, line, startCol));
    }

    /**
     * Scan an identifier (variable or keyword/function)
     */
    private void scanIdentifier() {
        int startPos = position;
        int startCol = column;

        while (position < input.length() && 
               (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            advance();
        }

        String lexeme = input.substring(startPos, position);
        TokenType type = TokenType.VARIABLE;

        // Check if it's a keyword/function
        if (KEYWORDS.contains(lexeme)) {
            switch (lexeme) {
                case "sin":   type = TokenType.SIN; break;
                case "cos":   type = TokenType.COS; break;
                case "tan":   type = TokenType.TAN; break;
                case "sqrt":  type = TokenType.SQRT; break;
                case "pow":   type = TokenType.POW; break;
                case "abs":   type = TokenType.ABS; break;
                case "log":   type = TokenType.LOG; break;
                case "exp":   type = TokenType.EXP; break;
                case "pi":    
                    type = TokenType.NUMBER;
                    tokens.add(new Token(type, lexeme, Math.PI, line, startCol));
                    return;
                case "e":
                    type = TokenType.NUMBER;
                    tokens.add(new Token(type, lexeme, Math.E, line, startCol));
                    return;
            }
        }

        tokens.add(new Token(type, lexeme, line, startCol));
    }

    /**
     * Skip a line comment (from # to end of line)
     */
    private void skipLineComment() {
        while (position < input.length() && peek() != '\n') {
            advance();
        }
    }

    /**
     * Add a token without a literal value
     */
    private void addToken(TokenType type, String lexeme) {
        tokens.add(new Token(type, lexeme, line, column));
    }

    /**
     * Peek at the current character
     */
    private char peek() {
        if (position >= input.length()) {
            return '\0';
        }
        return input.charAt(position);
    }

    /**
     * Peek at the next character
     */
    private char peekNext() {
        if (position + 1 >= input.length()) {
            return '\0';
        }
        return input.charAt(position + 1);
    }

    /**
     * Advance to the next character and update line/column
     */
    private void advance() {
        if (position < input.length()) {
            position++;
            column++;
        }
    }
}
