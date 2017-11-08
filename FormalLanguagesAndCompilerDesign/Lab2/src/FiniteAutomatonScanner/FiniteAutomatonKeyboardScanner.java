package FiniteAutomatonScanner;

import Interpreter.FiniteAutomaton.FiniteStateMachine;
import Interpreter.FiniteAutomaton.IFiniteStateMachine;
import Interpreter.FiniteAutomaton.State;
import Interpreter.FiniteAutomaton.Transition;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class FiniteAutomatonKeyboardScanner implements IFiniteAutomatonScanner {

    private List<State> states;
    private HashMap<Pair<Integer, Integer>, List<Character>> transitions;
    private Scanner input;
    private int index;

    public FiniteAutomatonKeyboardScanner() {
        states = new ArrayList<>();
        transitions = new HashMap<>();
        input = new Scanner(System.in);
        index = 0;
    }

    @Override
    public IFiniteStateMachine read() throws FiniteAutomatonScannerException {

        byte cmd = -1;

        while (cmd != 0) {
            printMenu();
            cmd = getCmd();
            doAction(cmd);
        }

        for (int i = 0; i < states.size(); ++i) {
            final int index = i;
            transitions.forEach((k, v) -> {
                if (k.getKey() == index) {
                    v.forEach(val -> {
                        states.get(index).with(new Transition(String.valueOf(val), states.get(k.getKey())));
                    });
                }
            });
        }

        return new FiniteStateMachine(states.get(0));
    }

    private void printMenu() {

        StringBuffer buffer = new StringBuffer();

        buffer.append("1. Enter state\n");
        buffer.append("0. Exit\n");

        System.out.println(buffer.toString());
    }

    private byte getCmd() {

        byte cmd;

        cmd = Byte.parseByte(input.nextLine());

        return cmd;
    }

    private void doAction(byte cmd) {

        switch (cmd) {
            case 1:
                boolean isFinal = getIsFinal();
                addTransition();

                states.add(new State(index++, isFinal));
                break;
            case 0:
                break;
            default:
                System.err.println("Invalid command!");
        }
    }

    private void addTransition() {

        char add;
        do {
            System.out.println("Enter from:");
            int from = Integer.parseInt(input.nextLine());

            System.out.println("Enter to:");
            int to = Integer.parseInt(input.nextLine());

            System.out.println("Enter with:");
            List<Character> with = Arrays.asList(input.nextLine().split("\\s+"))
                    .stream()
                    .map((s) -> s.charAt(0))
                    .collect(Collectors.toList());

            transitions.put(new Pair<>(from, to), with);

            System.out.println("Add another transition: (y/n)");
            add = input.nextLine().charAt(0);
        } while (add == 'y');
    }

    private boolean getIsFinal() {

        System.out.println("Is final?");
        char choice = input.nextLine().charAt(0);

        if (choice == 'y') {
            return true;
        }

        return false;
    }
}
