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
