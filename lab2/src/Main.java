import java.util.*;

// MAIN CLASS
public class Main {
    public static void main(String[] args) {
        System.out.println("=== VARIANT 12 NDFA ===\n");

        // Create NDFA for Variant 12
        FiniteAutomaton ndfa = new FiniteAutomaton();
        
        // Initialize states
        ndfa.Q.addAll(Arrays.asList("q0", "q1", "q2", "q3"));
        ndfa.sigma.addAll(Arrays.asList("a", "b", "c"));
        ndfa.q0 = "q0";
        ndfa.F.add("q2");

        // Add transitions (NDFA)
        ndfa.addTransition("q0", "b", "q0");
        ndfa.addTransition("q0", "a", "q1");
        ndfa.addTransition("q1", "c", "q1");
        ndfa.addTransition("q1", "a", "q2");
        ndfa.addTransition("q3", "a", "q1"); // Non-determinism
        ndfa.addTransition("q3", "a", "q3"); // Non-determinism
        ndfa.addTransition("q2", "a", "q3");

        // Print NDFA
        System.out.println("Original NDFA:");
        System.out.println(ndfa);

        // Check determinism
        System.out.println("Is Deterministic? " + ndfa.isDeterministic());
        System.out.println();

        // Test string acceptance on NDFA
        System.out.println("Testing NDFA string acceptance:");
        String[] testStrings = {"a", "ba", "bba", "ac", "aca", "acac", "acaa", "baaca", "aaaa"};
        for (String word : testStrings) {
            boolean accepted = ndfa.accepts(word);
            System.out.println("  Word \"" + word + "\" -> " + (accepted ? "Accepted" : "Rejected"));
        }
        System.out.println();

        // Convert NDFA to DFA
        System.out.println("\n=== CONVERSION TO DFA ===\n");
        FiniteAutomaton dfa = ndfa.convertNDFAtoDFA();
        System.out.println("Converted DFA:");
        System.out.println(dfa);

        // Check DFA determinism
        System.out.println("Is Deterministic? " + dfa.isDeterministic());
        System.out.println();

        // Test string acceptance on DFA
        System.out.println("Testing DFA string acceptance:");
        for (String word : testStrings) {
            boolean accepted = dfa.accepts(word);
            System.out.println("  Word \"" + word + "\" -> " + (accepted ? "Accepted" : "Rejected"));
        }
        System.out.println();

        // Convert NDFA to Grammar
        System.out.println("\n=== CONVERSION TO GRAMMAR ===\n");
        Grammar grammar = ndfa.toGrammar();
        System.out.println(grammar);

        // Convert DFA to Grammar
        System.out.println("\nGrammar from DFA:");
        Grammar dfaGrammar = dfa.toGrammar();
        System.out.println(dfaGrammar);
    }
}
