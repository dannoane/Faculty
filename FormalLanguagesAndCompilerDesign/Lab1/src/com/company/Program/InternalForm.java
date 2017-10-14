package com.company.Program;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InternalForm {

    private List<Pair<Integer, Integer>> internalForm;
    private HashMap<String, Integer> symbols;

    public InternalForm() {

        internalForm = new ArrayList<>();
        symbols = new HashMap<>();

        initialize();
    }

    public void add(String symbol, SymbolTable symbolTable) throws ProgramException {

        if (!symbols.containsKey(symbol)) {
            if (symbolTable.contains(symbol)) {
                internalForm.add(new Pair<>(symbols.get("ID"), symbolTable.get(symbol)));
            }
            else {
                internalForm.add(new Pair<>(symbols.get("ID"), symbolTable.set(symbol)));
            }
        }
        else {
            internalForm.add(new Pair<>(symbols.get(symbol), null));
        }
    }

    private void initialize() {

        symbols.put("(", 0);
        symbols.put(")", 1);
        symbols.put("newv", 2);
        symbols.put("struct", 3);
        symbols.put("integer", 4);
        symbols.put("real", 5);
        symbols.put("=", 6);
        symbols.put("+", 7);
        symbols.put("-", 8);
        symbols.put("*", 9);
        symbols.put("/", 11);
        symbols.put("%", 12);
        symbols.put("**", 13);
        symbols.put("<", 14);
        symbols.put(">", 15);
        symbols.put("<=", 16);
        symbols.put(">=", 17);
        symbols.put("==", 18);
        symbols.put("<>", 19);
        symbols.put("while", 20);
        symbols.put("if", 21);
        symbols.put("read", 22);
        symbols.put("write", 23);
        symbols.put("do", 24);
        symbols.put("ID", 25);
    }

    @Override
    public String toString() {
        return "InternalForm{" +
                "internalForm=" + internalForm +
                '}';
    }
}
