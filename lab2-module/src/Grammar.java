import java.util.*;

// GRAMMAR CLASS
public class Grammar {
    Set<String> VN; // Non-terminals
    Set<String> VT; // Terminals
    Map<String, List<String>> P; // Production rules
    String S; // Start symbol

    public Grammar() {
        this.VN = new HashSet<>();
        this.VT = new HashSet<>();
        this.P = new HashMap<>();
    }

    public void addRule(String lhs, String rhs) {
        P.computeIfAbsent(lhs, k -> new ArrayList<>()).add(rhs);
    }

    // VARIANT 12 INITIALIZATION (from Lab1)
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

    // GENERATE STRING (from Lab1)
    public String generateString() {
        StringBuilder word = new StringBuilder();
        String currentState = S;

        Random rand = new Random();
        System.out.print("Derivation: " + S); // Optional: checking derivation path

        while (true) {
            // Stop if we reach a state with no rules
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

    // GRAMMAR TO FINITE AUTOMATON CONVERSION (from Lab1)
    public FiniteAutomaton toFiniteAutomaton() {
        FiniteAutomaton fa = new FiniteAutomaton();
        fa.Q.addAll(this.VN);
        fa.Q.add("X"); // Special Final State for terminal transitions
        fa.sigma.addAll(this.VT);
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

                fa.addTransition(fromState, inputSymbol, nextState);
            }
        }
        return fa;
    }

    // Classify grammar according to Chomsky hierarchy
    public String classifyChomsky() {
        boolean isRegular = true;
        boolean isContextFree = true;

        for (String lhs : P.keySet()) {
            // Check if left-hand side is a single non-terminal
            if (lhs.length() != 1 || !VN.contains(lhs)) {
                isRegular = false;
                isContextFree = false;
                break;
            }

            for (String rhs : P.get(lhs)) {
                // Type 3 (Regular): A -> aB or A -> a or A -> ε
                // Right-linear or left-linear grammar
                boolean validRegular = isValidRegularProduction(rhs);

                if (!validRegular) {
                    isRegular = false;
                }

                // Type 2 (Context-Free): already satisfied by having single non-terminal on LHS
                // Type 1 (Context-Sensitive): |rhs| >= |lhs|
                if (rhs.length() < lhs.length() && !rhs.equals("ε")) {
                    isContextFree = false;
                }
            }
        }

        if (isRegular) {
            return "Type 3 (Regular Grammar)";
        } else if (isContextFree) {
            return "Type 2 (Context-Free Grammar)";
        } else {
            return "Type 0 (Recursively Enumerable) or Type 1 (Context-Sensitive)";
        }
    }

    private boolean isValidRegularProduction(String rhs) {
        // Valid regular (right-linear) productions: a, aA, ε where a is terminal, A is non-terminal
        if (rhs.equals("ε")) return true;

        if (rhs.length() == 1) {
            return VT.contains(rhs); // Single terminal
        }

        if (rhs.length() == 2) {
            return VT.contains(String.valueOf(rhs.charAt(0))) && 
                   VN.contains(String.valueOf(rhs.charAt(1)));
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grammar G = (VN, VT, P, S)\n");
        sb.append("VN = ").append(VN).append("\n");
        sb.append("VT = ").append(VT).append("\n");
        sb.append("S = ").append(S).append("\n");
        sb.append("P:\n");
        for (String lhs : P.keySet()) {
            for (String rhs : P.get(lhs)) {
                sb.append("  ").append(lhs).append(" -> ").append(rhs).append("\n");
            }
        }
        sb.append("Classification: ").append(classifyChomsky()).append("\n");
        return sb.toString();
    }
}
