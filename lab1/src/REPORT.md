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

* **Grammar Class**: I implemented a class capable of storing Non-terminals, Terminals, and Production rules. The `generateString` method uses random selection of production rules to construct a word until a terminal state is reached.
* **FiniteAutomaton Class**: This class represents the 5-tuple of an FA. The `stringBelongToLanguage` method iterates through the input string, updating the current state based on the transition map.
* **Conversion Logic**: The `toFiniteAutomaton` method maps grammar rules (e.g., $A \to aB$) to FA transitions (e.g., $\delta(A, a) = B$). If a rule leads to a terminal directly ($A \to a$), it transitions to a special Final State.

**Code Snippet (Grammar to FA):**
```java
public FiniteAutomaton toFiniteAutomaton() {
    // Logic mapping grammar productions to transition functions
    // ...
    fa.addTransition(fromState, inputSymbol, nextState);
    return fa;
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

## References

1. Course Materials: `task.md`
2. Java Documentation


