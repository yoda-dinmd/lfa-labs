/**
 * Token class representing a lexical token
 * Contains token type, lexeme (actual text), line, and column information
 */
public class Token {
    private TokenType type;
    private String lexeme;
    private Object literal;  // For storing numeric values or other metadata
    private int line;
    private int column;

    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, null, line, column);
    }

    // Getters
    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("Token(%s, '%s', line=%d, col=%d%s)",
                type, lexeme, line, column,
                literal != null ? ", literal=" + literal : "");
    }
}
