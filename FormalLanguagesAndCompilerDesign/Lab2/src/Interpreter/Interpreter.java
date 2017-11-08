package Interpreter;

import Program.InternalForm;
import Program.SymbolTable;
import Scanner.Scanner;
import Scanner.ScannerException;

public class Interpreter {

    private String filename;

    public Interpreter(String filename) {

        this.filename = filename;
    }

    public void interpret() throws InterpreterException {

        InternalForm internalForm = new InternalForm();
        SymbolTable symbolTable = new SymbolTable();
        Scanner scanner = new Scanner(filename, internalForm, symbolTable);

        try {
            scanner.parse();
        } catch (ScannerException e) {
            throw new InterpreterException(e.getMessage());
        }

        System.out.println(internalForm.toString());
        System.out.println(symbolTable.toString());
    }
}
