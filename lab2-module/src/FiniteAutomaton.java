import java.util.*;
import java.util.stream.Collectors;

// FINITE AUTOMATON CLASS
public class FiniteAutomaton {
    Set<String> Q; // States
    Set<String> sigma; // Alphabet
    Map<String, Map<String, Set<String>>> delta; // Transition function (non-deterministic)
    String q0; // Start state
    Set<String> F; // Final states

    public FiniteAutomaton() {
        this.Q = new HashSet<>();
        this.sigma = new HashSet<>();
        this.delta = new HashMap<>();
        this.F = new HashSet<>();
    }

    // Add a transition (supports non-determinism)
    public void addTransition(String from, String input, String to) {
        delta.computeIfAbsent(from, k -> new HashMap<>())
             .computeIfAbsent(input, k -> new HashSet<>())
             .add(to);
    }

    // Check if FA is deterministic
    public boolean isDeterministic() {
        for (Map<String, Set<String>> stateTransitions : delta.values()) {
            for (Set<String> destinations : stateTransitions.values()) {
                if (destinations.size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    // Get epsilon closure (for future epsilon transitions if needed)
    private Set<String> epsilonClosure(Set<String> states) {
        Set<String> closure = new HashSet<>(states);
        Queue<String> queue = new LinkedList<>(states);
        
        while (!queue.isEmpty()) {
            String state = queue.poll();
            if (delta.containsKey(state) && delta.get(state).containsKey("ε")) {
                for (String nextState : delta.get(state).get("ε")) {
                    if (!closure.contains(nextState)) {
                        closure.add(nextState);
                        queue.add(nextState);
                    }
                }
            }
        }
        return closure;
    }

    // Convert NDFA to DFA using subset construction
    public FiniteAutomaton convertNDFAtoDFA() {
        if (isDeterministic()) {
            return this;
        }

        FiniteAutomaton dfa = new FiniteAutomaton();
        dfa.sigma = new HashSet<>(this.sigma);

        // Map subset states to DFA states
        Map<Set<String>, String> subsetToDFA = new HashMap<>();
        Queue<Set<String>> workList = new LinkedList<>();
        
        Set<String> startSubset = epsilonClosure(Set.of(q0));
        workList.add(startSubset);
        subsetToDFA.put(startSubset, dfaStateName(startSubset));
        dfa.q0 = dfaStateName(startSubset);

        // Subset construction
        while (!workList.isEmpty()) {
            Set<String> currentSubset = workList.poll();
            String dfaState = subsetToDFA.get(currentSubset);
            dfa.Q.add(dfaState);

            // Check if any state in subset is final
            if (currentSubset.stream().anyMatch(s -> F.contains(s))) {
                dfa.F.add(dfaState);
            }

            // For each input symbol
            for (String symbol : sigma) {
                Set<String> nextSubset = new HashSet<>();
                
                for (String state : currentSubset) {
                    if (delta.containsKey(state) && delta.get(state).containsKey(symbol)) {
                        nextSubset.addAll(delta.get(state).get(symbol));
                    }
                }

                if (!nextSubset.isEmpty()) {
                    nextSubset = epsilonClosure(nextSubset);
                    String nextDFAState = dfaStateName(nextSubset);

                    if (!subsetToDFA.containsKey(nextSubset)) {
                        subsetToDFA.put(nextSubset, nextDFAState);
                        workList.add(nextSubset);
                    }

                    dfa.addTransition(dfaState, symbol, subsetToDFA.get(nextSubset));
                }
            }
        }

        return dfa;
    }

    // Convert FA to regular grammar
    public Grammar toGrammar() {
        Grammar grammar = new Grammar();
        grammar.VN = new HashSet<>(this.Q);
        grammar.VT = new HashSet<>(this.sigma);
        grammar.S = this.q0;

        // For each transition δ(p, a) = q, add production p -> aq
        for (String from : delta.keySet()) {
            for (String input : delta.get(from).keySet()) {
                for (String to : delta.get(from).get(input)) {
                    String production = input + to;
                    grammar.addRule(from, production);
                }
            }
        }

        // For each final state, add production q -> ε
        for (String finalState : F) {
            grammar.addRule(finalState, "ε");
        }

        return grammar;
    }

    // Helper: create DFA state name from subset
    private String dfaStateName(Set<String> subset) {
        return "{" + subset.stream().sorted().collect(Collectors.joining(",")) + "}";
    }

    // Check if a string is accepted
    public boolean accepts(String word) {
        Set<String> currentStates = epsilonClosure(Set.of(q0));

        for (char ch : word.toCharArray()) {
            Set<String> nextStates = new HashSet<>();
            for (String state : currentStates) {
                if (delta.containsKey(state) && delta.get(state).containsKey(String.valueOf(ch))) {
                    nextStates.addAll(delta.get(state).get(String.valueOf(ch)));
                }
            }
            currentStates = epsilonClosure(nextStates);
        }

        return currentStates.stream().anyMatch(s -> F.contains(s));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FA = (Q, Σ, δ, q0, F)\n");
        sb.append("Q = ").append(Q).append("\n");
        sb.append("Σ = ").append(sigma).append("\n");
        sb.append("q0 = ").append(q0).append("\n");
        sb.append("F = ").append(F).append("\n");
        sb.append("δ:\n");
        for (String from : delta.keySet()) {
            for (String input : delta.get(from).keySet()) {
                for (String to : delta.get(from).get(input)) {
                    sb.append("  δ(").append(from).append(",").append(input).append(") = ").append(to).append("\n");
                }
            }
        }
        return sb.toString();
    }
}
