package Interpreter.FiniteAutomaton;

import java.util.List;

public interface IState {

    IState with(ITransition transition);
    IState transit(CharSequence c);
    boolean isFinal();
    List<IState> nextStates();
    boolean visited();
    void visit();
    List<ITransition> getTransitions();
    String toString();
}
