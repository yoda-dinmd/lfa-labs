import java.util.*;

// 2. CLASS: FINITE AUTOMATON
class FiniteAutomaton {
    Set<String> Q = new HashSet<>();
    Set<String> Sigma = new HashSet<>();
    Map<String, Map<Character, String>> delta = new HashMap<>();
    String q0;
    Set<String> F = new HashSet<>();

    public void addTransition(String from, char input, String to) {
        delta.computeIfAbsent(from, k -> new HashMap<>()).put(input, to);
    }

    public boolean stringBelongToLanguage(String inputString) {
        String currentState = q0;

        for (char ch : inputString.toCharArray()) {
            if (!delta.containsKey(currentState) || !delta.get(currentState).containsKey(ch)) {
                return false;
            }
            currentState = delta.get(currentState).get(ch);
        }

        return F.contains(currentState);
    }
}
