package Interpreter.FiniteAutomaton;

public class Transition implements ITransition {

    private String rule;
    private State next;

    public Transition(String rule, State next) {
        this.rule = rule;
        this.next = next;
    }

    @Override
    public boolean isPossible(CharSequence c) {
        return rule.equalsIgnoreCase(String.valueOf(c));
    }

    @Override
    public IState state() {
        return next;
    }

    @Override
    public String getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "rule='" + rule + '\'' +
                ", next=" + next +
                '}';
    }
}
