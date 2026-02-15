import java.util.ArrayList;
import java.util.List;

// 3. MAIN CLASS
public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        grammar.initGrammar();

        System.out.println("=== Variant 12 Generation ===");
        List<String> generatedWords = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            generatedWords.add(grammar.generateString());
        }

        System.out.println("\nGenerated Words: " + generatedWords);

        FiniteAutomaton fa = grammar.toFiniteAutomaton();

        System.out.println("\n=== FA Validation ===");
        for (String word : generatedWords) {
            boolean valid = fa.stringBelongToLanguage(word);
            System.out.println("Word: " + word + " -> Accepted: " + valid);
        }

        // Optional: Test an invalid word
        System.out.println("Word: aaabbb (Invalid) -> Accepted: " + fa.stringBelongToLanguage("aaabbb"));
    }
}