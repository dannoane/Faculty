package com.company.Program;

import java.util.SortedMap;
import java.util.TreeMap;

public class SymbolTable {

    private SortedMap<String, Integer> symbolTable;
    private int constCode = -1;
    private int varCode = 1;


    public SymbolTable() {

        symbolTable = new TreeMap<>();
    }

    public int set(String id, boolean constant) throws ProgramException {

        if (symbolTable.containsKey(id)) {
            throw new ProgramException(id + " is already defined");
        }

        if (!constant && id.matches("^([a-zA-Z_$]+[0-9]*)+$")) {
            symbolTable.put(id, varCode);
            return varCode++;
        }
        else if ((constant && id.matches("^([a-zA-Z_$]+[0-9]*)+$")) || id.matches("^[0-9]+(\\.[0-9]+)?")) {
            symbolTable.put(id, constCode);
            return constCode--;
        }
        else {
            throw new ProgramException(id + " is an invalid identifier");
        }
    }

    public boolean contains(String id) {

        return symbolTable.containsKey(id);
    }

    public int get(String id) {

        return symbolTable.get(id);
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "symbolTable=" + symbolTable +
                '}';
    }
}
