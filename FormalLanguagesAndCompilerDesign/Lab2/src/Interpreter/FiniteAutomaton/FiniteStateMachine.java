package Interpreter.FiniteAutomaton;

import java.util.*;
import java.util.stream.Collectors;

public class FiniteStateMachine implements IFiniteStateMachine {

    private IState current;

    public FiniteStateMachine(IState initial) {
        this.current = initial;
    }

    @Override
    public FiniteStateMachine switchCase(CharSequence c) {
        return new FiniteStateMachine(current.transit(c));
    }

    @Override
    public boolean canStop() {
        return current.isFinal();
    }

    @Override
    public void print() {

        StringBuffer buffer = new StringBuffer();
        Queue<IState> queue = new ArrayDeque<>();
        HashMap<IState, List<ITransition>> transitions = new HashMap<>();
        List<String> alphabet = new ArrayList<>();

        queue.add(current);

        buffer.append("States={\n");
        while (!queue.isEmpty()) {
            IState state = queue.remove();
            state.visit();

            buffer.append("\t" + state.toString() + "\n");

            transitions.put(state, state.getTransitions());
            queue.addAll(state
                    .nextStates()
                    .stream()
                    .filter(s -> !s.visited())
                    .collect(Collectors.toList())
            );
        }
        buffer.append("}\n");

        buffer.append("Transitions={\n");
        transitions.forEach((k, v) -> {
            buffer.append("\tFrom: " + k.toString() + "\n");
            v.forEach(t -> {
                buffer.append("\t\t" + t.toString() + "\n");
                alphabet.add(t.getRule());
            });
        });
        buffer.append("}\n");

        buffer.append("Alphabet: " + alphabet
                .stream()
                .distinct()
                .collect(Collectors.toList())
        );

        System.out.println(buffer.toString());
    }
}
