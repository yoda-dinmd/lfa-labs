import java.util.*;

// 1. CLASS: GRAMMAR
class Grammar {
    Set<String> VN; // Non-terminals
    Set<String> VT; // Terminals
    Map<String, List<String>> P; // Production rules
    String S; // Start symbol

    public Grammar() {
        this.VN = new HashSet<>();
        this.VT = new HashSet<>();
        this.P = new HashMap<>();
    }

    // SPECIFIC CONFIGURATION FOR VARIANT 12
    public void initGrammar() {
        // Define VN = {S, F, D}
        this.VN.add("S");
        this.VN.add("F");
        this.VN.add("D");

        // Define VT = {a, b, c}
        this.VT.add("a");
        this.VT.add("b");
        this.VT.add("c");

        // Define Start Symbol
        this.S = "S";

        // Define Productions P
        // S -> aF
        addRule("S", "aF");
        // S -> bS
        addRule("S", "bS");

        // F -> bF
        addRule("F", "bF");
        // F -> cD
        addRule("F", "cD");
        // F -> a (Terminal rule)
        addRule("F", "a");

        // D -> cS
        addRule("D", "cS");
        // D -> a (Terminal rule)
        addRule("D", "a");
    }

    private void addRule(String key, String value) {
        P.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public String generateString() {
        StringBuilder word = new StringBuilder();
        String currentState = S;

        Random rand = new Random();
        System.out.print("Derivation: " + S); // Optional: checking derivation path

        while (true) {
            // Stop if we reach a state with no rules (shouldn't happen in your variant unless final)
            if (!P.containsKey(currentState)) {
                break;
            }

            List<String> rules = P.get(currentState);
            String nextRule = rules.get(rand.nextInt(rules.size()));

            System.out.print(" -> " + word + nextRule); // Optional: visual debug

            // Parse rule logic for Right Linear Grammar (Type 3)
            // Expecting formats like "aF" or "a"

            String terminal = nextRule.substring(0, 1);
            word.append(terminal);

            if (nextRule.length() > 1) {
                // Case: S -> aF (continue to F)
                currentState = nextRule.substring(1);
            } else {
                // Case: F -> a (stop, terminal reached)
                break;
            }
        }
        System.out.println(); // New line after derivation
        return word.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        FiniteAutomaton fa = new FiniteAutomaton();
        fa.Q.addAll(this.VN);
        fa.Q.add("X"); // Special Final State for terminal transitions
        fa.Sigma.addAll(this.VT);
        fa.q0 = this.S;
        fa.F.add("X"); // "X" is the state we reach when the word is finished successfully

        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            String fromState = entry.getKey();
            for (String production : entry.getValue()) {
                String inputSymbol = production.substring(0, 1);

                // Logic for Variant 12 rules
                // If rule is "aF", go to "F".
                // If rule is "a", go to "X" (Final).
                String nextState = (production.length() > 1) ? production.substring(1) : "X";

                fa.addTransition(fromState, inputSymbol.charAt(0), nextState);
            }
        }
        return fa;
    }
}