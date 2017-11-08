package Interpreter.FiniteAutomaton;

public interface ITransition {

    boolean isPossible(CharSequence c);
    IState state();
    String getRule();
    String toString();
}
