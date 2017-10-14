package com.company.Program;

import java.util.SortedMap;
import java.util.TreeMap;

public class SymbolTable {

    private SortedMap<String, Integer> symbolTable;
    private int code = 0;


    public SymbolTable() {

        symbolTable = new TreeMap<>();
    }

    public int set(String id) throws ProgramException {

        if (symbolTable.containsKey(id))
            throw new ProgramException(id + " is already defined");

        symbolTable.put(id, code);

        return code++;
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
