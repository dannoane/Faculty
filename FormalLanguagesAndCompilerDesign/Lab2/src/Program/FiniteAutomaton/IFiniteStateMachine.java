package Program.FiniteAutomaton;

public interface IFiniteStateMachine {

    IFiniteStateMachine switchCase(CharSequence c);
    boolean canStop();
    void print();
}
