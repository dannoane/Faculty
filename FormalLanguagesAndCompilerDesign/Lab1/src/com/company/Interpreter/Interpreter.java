package com.company.Interpreter;

import com.company.Program.InternalForm;
import com.company.Program.SymbolTable;
import com.company.Scanner.Scanner;
import com.company.Scanner.ScannerException;

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
