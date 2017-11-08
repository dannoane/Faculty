package Interpreter.FiniteAutomaton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class State implements IState {

    private List<ITransition> transitions;
    private boolean isFinal;
    private boolean visited;
    private int index;

    public State(int index) {
        this(index, false);
    }

    public State(int index, boolean isFinal) {

        this.transitions = new ArrayList<>();
        this.isFinal = isFinal;
        this.visited = false;
        this.index = index;
    }

    @Override
    public IState transit(CharSequence c) {

        return transitions
                .stream()
                .filter(t -> t.isPossible(c))
                .map(ITransition::state)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Input not accepted: " + c));
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public IState with(ITransition tr) {

        transitions.add(tr);
        return this;
    }

    @Override
    public List<IState> nextStates() {

        List<IState> nextStates = transitions
                .stream()
                .map(ITransition::state)
                .distinct()
                .collect(Collectors.toList());

        return nextStates;
    }

    @Override
    public boolean visited() {
        return this.visited;
    }

    @Override
    public void visit() {
        this.visited = true;
    }

    @Override
    public List<ITransition> getTransitions() {
        return transitions;
    }

    @Override
    public String toString() {
        return "State{" +
                "isFinal=" + isFinal +
                ", index=" + index +
                '}';
    }
}
