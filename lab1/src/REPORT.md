# Intro to formal languages. Regular grammars. Finite Automata.

### Course: Formal Languages & Finite Automata
### Author: Gafenco Victor, FAF-241

----

## Theory
A formal language is defined by an alphabet, a vocabulary, and a grammar. The grammar consists of Non-terminals ($V_N$), Terminals ($V_T$), Production Rules ($P$), and a Start Symbol ($S$).

A Finite Automaton (FA) is a mechanism used to recognize patterns within input data. It consists of States ($Q$), an Alphabet ($\Sigma$), a Transition Function ($\delta$), a Start State ($q_0$), and Final States ($F$). There is a direct conversion capability between Regular Grammars (Type 3) and Finite Automata.

## Objectives:

* Discover what a language is and what it needs to have in order to be considered a formal one.
* Setup the GitHub repository and project structure.
* Implement a `Grammar` class to generate valid strings.
* Implement a conversion method from `Grammar` to `FiniteAutomaton`.
* Implement a `FiniteAutomaton` class to check if a string belongs to the language.

## Implementation description

The implementation consists of two main components: a Grammar class that generates strings according to defined production rules, and a FiniteAutomaton class that validates whether strings belong to the language. The conversion between these two representations forms the core of the lab.

The **Grammar Class** was designed to store the essential components of a regular grammar: non-terminals, terminals, and production rules organized by their left-hand side. The `generateString` method operates by starting from the initial symbol and iteratively applying randomly selected production rules. At each step, if the current state is a non-terminal, the method picks one of its available production rules at random and applies it. This process continues until only terminal symbols remain, producing a valid word in the language. This stochastic approach ensures diversity in generated strings while maintaining correctness according to the grammar definition.

The **FiniteAutomaton Class** implements the formal definition of a 5-tuple (Q, Σ, δ, q₀, F). The core validation logic resides in the `stringBelongToLanguage` method, which processes input sequentially. For each character in the input string, the method performs a state transition using the precomputed transition function. If at any point no valid transition exists for the current state and input symbol, the string is rejected. The automaton accepts the string only if we reach a final state after consuming all input characters.

The **Conversion Logic** bridges these two representations by transforming grammar production rules into FA transitions. For productions of the form $A \to aB$ where both terminals and non-terminals are involved, the conversion creates a transition $\delta(A, a) = B$. Productions where a non-terminal directly generates a terminal symbol ($A \to a$) are handled specially by introducing transitions to a dedicated final state. This conversion ensures that any string accepted by the FA would be derivable from the grammar, and vice versa, maintaining the semantic equivalence between the two models.

**Key Implementation Examples:**

The core logic for converting a grammar to a finite automaton processes all production rules, mapping them to state transitions. For terminal rules (e.g., `F -> a`), a special final state `X` is introduced to mark acceptance:

```java
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
            String nextState = (production.length() > 1) ? production.substring(1) : "X";
            fa.addTransition(fromState, inputSymbol.charAt(0), nextState);
        }
    }
    return fa;
}
```

The FA validation processes input character by character through the transition function, accepting only if we reach a final state:

```java
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
```

## Results

**Console Output:**

```text
=== Variant 12 Generation ===
Derivation: S -> bS -> bbS -> bbbS -> bbbbS -> bbbbbS -> bbbbbbS -> bbbbbbaF -> bbbbbbabF -> bbbbbbabcD -> bbbbbbabccS -> bbbbbbabccaF -> bbbbbbabccaa
Derivation: S -> bS -> baF -> bacD -> baca
Derivation: S -> bS -> bbS -> bbaF -> bbabF -> bbabcD -> bbabca
Derivation: S -> bS -> baF -> babF -> baba
Derivation: S -> aF -> aa

Generated Words: [bbbbbbabccaa, baca, bbabca, baba, aa]

=== FA Validation ===
Word: bbbbbbabccaa -> Accepted: true
Word: baca -> Accepted: true
Word: bbabca -> Accepted: true
Word: baba -> Accepted: true
Word: aa -> Accepted: true
Word: aaabbb (Invalid) -> Accepted: false

```

## Difficulties and Challenges

The primary challenge encountered during this lab involved correctly handling the conversion from grammar production rules to finite automaton transitions. Regular grammars can contain productions with various forms, and not all of them map directly to deterministic state transitions. Specifically, differentiating between productions where a non-terminal remains in the derivation (e.g., $A \to aB$) and productions where only a terminal is generated (e.g., $A \to a$) required careful state management. The second case necessitated the introduction of a specially designated final state to ensure that the automaton properly recognizes end conditions.

Another significant difficulty was ensuring consistency between the Grammar's string generation and the FiniteAutomaton's string validation. Since the Grammar employs random selection of production rules during generation, not all productions may be exercised in a single run. This made it critical to ensure that the FiniteAutomaton was constructed with transitions for all possible productions, not just those used in recent generations. Otherwise, grammatically valid strings might be rejected by the automaton due to incomplete transition tables.

Additionally, managing the state space required attention to detail. Each non-terminal symbol naturally becomes a state in the automaton, but handling terminal productions required explicit treatment to avoid ambiguity. The solution involved treating terminals that directly end derivations as transitions into final states, though care had to be taken to ensure these final states were properly registered in the automaton's final state set.

## References

1. Course Materials: `task.md`
2. Java Documentation


