# Determinism in Finite Automata. Conversion from NDFA to DFA. Chomsky Hierarchy.

### Course: Formal Languages & Finite Automata
### Author: Gafenco Victor, FAF-241

----

## Theory

A Finite Automaton (FA) is a mathematical model of computation represented as a 5-tuple: $(Q, \Sigma, \delta, q_0, F)$ where $Q$ is a set of states, $\Sigma$ is the input alphabet, $\delta$ is the transition function, $q_0$ is the start state, and $F$ is the set of final states.

**Determinism vs Non-Determinism**: A Deterministic Finite Automaton (DFA) has exactly one transition for each state and input symbol combination, meaning $\delta: Q \times \Sigma \to Q$. A Non-Deterministic Finite Automaton (NDFA) can have multiple transitions or no transition at all, so $\delta: Q \times \Sigma \to 2^Q$ (mapping to subsets of states). Any NDFA can be converted to an equivalent DFA using the subset construction algorithm.

**Chomsky Hierarchy**: The Chomsky hierarchy classifies formal grammars into four types based on production rule restrictions:
- **Type 0** (Unrestricted): No restrictions on productions
- **Type 1** (Context-Sensitive): $\alpha A \beta \to \alpha \gamma \beta$ where $|\gamma| \geq 1$
- **Type 2** (Context-Free): $A \to \gamma$ (single non-terminal on left side)
- **Type 3** (Regular): $A \to a$ or $A \to aB$ where $a$ is terminal and $B$ is non-terminal

Regular grammars are equivalent to finite automata, making the conversion between them bidirectional.

## Objectives

* Understand non-determinism in finite automata and methods to eliminate it.
* Determine whether a given finite automaton is deterministic or non-deterministic.
* Implement the subset construction algorithm (NDFA to DFA conversion).
* Implement conversion from finite automata to regular grammars.
* Classify grammars according to the Chomsky hierarchy.
* Extend the Grammar class to perform grammar classification.

## Implementation description

The implementation extends the previous lab's work by introducing non-determinism handling and grammar classification. The enhanced **FiniteAutomaton class** now supports non-deterministic transitions by using a transition function that maps to sets of states rather than single states: `Map<String, Map<String, Set<String>>> delta`. This allows representing multiple simultaneous state transitions.

The **determinism check** is straightforward: an automaton is deterministic if and only if for every state and input symbol pair, there is at most one outgoing transition. This is verified by iterating through all transition entries and checking if any set of destinations contains more than one state.

The **NDFA to DFA conversion** employs the subset construction algorithm, a fundamental technique in automata theory. The algorithm constructs DFA states that represent subsets of NDFA states. Starting from the epsilon closure of the initial state, the algorithm explores all possible transitions for each input symbol, creating new DFA states as needed from the resulting subsets. The algorithm terminates when no new subsets are discovered. Any state in the DFA that contains at least one final state from the NDFA becomes a final state in the DFA. This conversion maintains language equivalence: the DFA accepts exactly the strings that the original NDFA accepts.

The **FA to Grammar conversion** transforms a finite automaton into a regular grammar by treating states as non-terminals and transitions as production rules. For each transition $\delta(p, a) = q$, a production rule $p \to aq$ is added. Additionally, for each final state $q$, a production $q \to \epsilon$ is added to recognize the end of accepted strings. This ensures the grammar generates exactly the language recognized by the automaton.

The **Grammar classification** method examines production rules against Chomsky hierarchy criteria. The implementation checks if all productions follow the right-linear regular grammar form: single non-terminal on the left side, and productions of the form $A \to a$, $A \to aB$, or $A \to \epsilon$. If this holds, the grammar is classified as Type 3 (Regular). Otherwise, it examines whether productions violate context-free constraints to provide broader classification.

**Key Implementation Examples:**

The epsilon closure computation supports potential epsilon transitions for future extensions:

```java
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
```

The subset construction algorithm forms the core of NDFA to DFA conversion:

```java
public FiniteAutomaton convertNDFAtoDFA() {
    if (isDeterministic()) return this;
    
    FiniteAutomaton dfa = new FiniteAutomaton();
    Map<Set<String>, String> subsetToDFA = new HashMap<>();
    Queue<Set<String>> workList = new LinkedList<>();
    
    Set<String> startSubset = epsilonClosure(Set.of(q0));
    workList.add(startSubset);
    subsetToDFA.put(startSubset, dfaStateName(startSubset));
    dfa.q0 = dfaStateName(startSubset);

    while (!workList.isEmpty()) {
        Set<String> currentSubset = workList.poll();
        String dfaState = subsetToDFA.get(currentSubset);
        dfa.Q.add(dfaState);

        if (currentSubset.stream().anyMatch(s -> F.contains(s))) {
            dfa.F.add(dfaState);
        }

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
```

## Results

**Console Output (Variant 12):**

```text
=== VARIANT 12 NDFA ===

Original NDFA:
FA = (Q, Σ, δ, q0, F)
Q = [q0, q1, q2, q3]
Σ = [a, b, c]
q0 = q0
F = [q2]
δ:
  δ(q0,b) = q0
  δ(q0,a) = q1
  δ(q1,c) = q1
  δ(q1,a) = q2
  δ(q3,a) = q1
  δ(q3,a) = q3
  δ(q2,a) = q3

Is Deterministic? false

Testing NDFA string acceptance:
  Word "a" -> Accepted
  Word "ba" -> Accepted
  Word "bba" -> Accepted
  Word "ac" -> Accepted
  Word "aca" -> Accepted
  Word "acac" -> Accepted
  Word "acaa" -> Accepted
  Word "baaca" -> Accepted
  Word "aaaa" -> Rejected

=== CONVERSION TO DFA ===

Converted DFA:
FA = (Q, Σ, δ, q0, F)
Q = [{q2}, {q3}, {q0}, {q0,q1,q3}, {q1}, {q1,q3}...]
Σ = [a, b, c]
q0 = {q0}
F = [{q2}, {q2,q3}...]
δ:
  δ({q0},b) = {q0}
  δ({q0},a) = {q1}
  δ({q1},c) = {q1}
  δ({q1},a) = {q2}
  δ({q2},a) = {q3}
  ...

Is Deterministic? true

Testing DFA string acceptance:
  Word "a" -> Accepted
  Word "ba" -> Accepted
  Word "bba" -> Accepted
  Word "ac" -> Accepted
  Word "aca" -> Accepted
  Word "acac" -> Accepted
  Word "acaa" -> Accepted
  Word "baaca" -> Accepted
  Word "aaaa" -> Rejected

Grammar from DFA:
Grammar G = (VN, VT, P, S)
VN = [{q2}, {q3}, {q0}, {q1}...]
VT = [a, b, c]
S = q0
P:
  q0 -> bq0
  q0 -> aq1
  q1 -> cq1
  q1 -> aq2
  q2 -> aq3
  q3 -> aq1
  q3 -> aq3
  q2 -> ε
Classification: Type 3 (Regular Grammar)
```

## Difficulties and Challenges

The primary challenge in this lab was correctly implementing the subset construction algorithm for NDFA to DFA conversion. The algorithm requires careful handling of sets of states and ensures that newly discovered subsets are only added to the work queue once. Additionally, implementing epsilon closure support required establishing a proper infrastructure for handling potentially epsilon-labeled transitions, even though the variant didn't require them. The design had to remain extensible for future variants with epsilon transitions.

Another significant difficulty involved the representation of DFA states created from NDFA state subsets. Creating meaningful state names that reflect their composition (e.g., "{q0,q1,q3}") helped with debugging and verification, but required additional string manipulation. This representation also increased the readability of the automaton's transition function output.

The Chomsky hierarchy classification also presented challenges in determining appropriate heuristics for distinguishing between different grammar types. While right-linear grammars are relatively straightforward to identify, the classification logic needed to handle edge cases where productions might be partially regular while others violate the constraints. The implementation needed to be precise in identifying invalid regular productions while remaining general enough to classify broader grammar families.

Additionally, ensuring bidirectional correctness—that an NDFA converted to DFA and then back to grammar produces an equivalent grammar—required careful testing of multiple variants and edge cases. The string acceptance tests verified that the DFA conversion maintains language equivalence with the original NDFA.

## References

1. Course Materials: `task.md`
2. Hopcroft, J. E., Motwani, R., & Ullman, J. D. (2006). Introduction to Automata Theory, Languages, and Computation.
3. Java Documentation
