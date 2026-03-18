/**
 * TokenType enumeration for mathematical expression lexer
 * Defines all possible token types in the language
 */
public enum TokenType {
    // Literals
    NUMBER,         // 123, 45.67
    VARIABLE,       // x, y, pi, e
    
    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    MODULO,         // %
    POWER,          // ^ or **
    
    // Functions
    SIN,            // sin
    COS,            // cos
    TAN,            // tan
    SQRT,           // sqrt
    POW,            // pow
    ABS,            // abs
    LOG,            // log
    EXP,            // exp
    
    // Delimiters
    LPAREN,         // (
    RPAREN,         // )
    COMMA,          // ,
    
    // Special
    EOF,            // End of file
    UNKNOWN         // Unknown token
}
