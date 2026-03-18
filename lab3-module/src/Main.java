import java.util.*;

/**
 * Main class demonstrating the mathematical expression lexer
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== LEXICAL ANALYSIS: MATHEMATICAL EXPRESSION LEXER ===\n");

        // Test cases
        String[] testExpressions = {
                "3.14 + 2.71",                          // Simple arithmetic
                "sin(x) + cos(y)",                      // Trigonometric functions
                "(2 * x) ^ 3 + sqrt(16)",              // Power and sqrt
                "abs(-5) + log(10)",                    // Absolute value and logarithm
                "pow(2, 8) - exp(1)",                   // Power and exponential functions
                "pi * r^2",                             // Constants and variables
                "tan(45) * sin(30) / cos(60)",         // Complex trigonometric
                "sqrt(x^2 + y^2)",                      // Distance formula
                "# This is a comment\nsin(x) + 1",      // Comments and multiline
                "123.456 * (a + b) % 10",              // Mixed operators
                "2e3",                                   // Scientific notation (edge case)
                "_var1 + variable_2",                   // Variables with underscores
        };

        for (String expression : testExpressions) {
            System.out.println("Expression: " + expression);
            System.out.println("Tokens:");
            
            Lexer lexer = new Lexer(expression);
            List<Token> tokens = lexer.tokenize();
            
            for (Token token : tokens) {
                if (token.getType() != TokenType.EOF) {
                    System.out.println("  " + token);
                }
            }
            System.out.println();
        }

        // Demonstration of error handling
        System.out.println("\n=== ERROR HANDLING ===\n");
        String invalidExpression = "3.14 + @ ^ &";
        System.out.println("Expression: " + invalidExpression);
        System.out.println("Tokens:");
        
        Lexer lexer = new Lexer(invalidExpression);
        List<Token> tokens = lexer.tokenize();
        
        for (Token token : tokens) {
            if (token.getType() == TokenType.UNKNOWN) {
                System.out.println("  WARNING: " + token);
            } else if (token.getType() != TokenType.EOF) {
                System.out.println("  " + token);
            }
        }
    }
}
