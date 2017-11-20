package Program;

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

        if (constant) {
            symbolTable.put(id, constCode);
            return constCode--;
        }
        else {
            symbolTable.put(id, varCode);
            return varCode++;
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
