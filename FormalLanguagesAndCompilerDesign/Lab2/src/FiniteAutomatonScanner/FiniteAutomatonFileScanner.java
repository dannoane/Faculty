package FiniteAutomatonScanner;

import Interpreter.FiniteAutomaton.FiniteStateMachine;
import Interpreter.FiniteAutomaton.IFiniteStateMachine;
import Interpreter.FiniteAutomaton.State;
import Interpreter.FiniteAutomaton.Transition;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FiniteAutomatonFileScanner implements IFiniteAutomatonScanner {

    private String filename;

    public FiniteAutomatonFileScanner(String filename) {
        this.filename = filename;
    }

    @Override
    public IFiniteStateMachine read() throws FiniteAutomatonScannerException {

        String data = readFiniteAutomaton();
        JSONObject finiteAutomaton = new JSONObject(data);
        JSONArray finiteAutomatonStates = finiteAutomaton.getJSONArray("states");
        List<State> states = new ArrayList<>();

        for (int i = 0; i < finiteAutomatonStates.length(); ++i) {
            JSONObject state = finiteAutomatonStates.getJSONObject(i);
            states.add(new State(i, state.getBoolean("isFinal")));
        }

        for (int i = 0; i < finiteAutomatonStates.length(); ++i) {
            JSONObject state = finiteAutomatonStates.getJSONObject(i);
            JSONArray transitions = state.getJSONArray("transitions");

            for (int j = 0; j < transitions.length(); ++j) {
                JSONObject transition = transitions.getJSONObject(j);
                int to = transition.getInt("to");
                JSONArray with = transition.getJSONArray("with");

                for (int k = 0; k < with.length(); ++k) {
                    states.get(i).with(new Transition(with.getString(k), states.get(to)));
                }
            }
        }

        return new FiniteStateMachine(states.get(0));
    }

    private String readFiniteAutomaton() throws FiniteAutomatonScannerException {

        String data;

        try {
            data = new String(Files.readAllBytes(Paths.get(filename)));
        }
        catch (IOException ex) {
            throw new FiniteAutomatonScannerException(ex.getMessage());
        }

        return data;
    }
}
