package Scanner;

import Program.InternalForm;
import Program.ProgramException;
import Program.SymbolTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class Scanner {

    private String filename;
    private InternalForm internalForm;
    private SymbolTable symbolTable;
    private String program;

    public Scanner(String filename, InternalForm internalForm, SymbolTable symbolTable) {

        this.filename = filename;
        this.internalForm = internalForm;
        this.symbolTable = symbolTable;
    }

    private void read() throws ScannerException {

        try {
            this.program  = new String(Files.readAllBytes(Paths.get(filename)));
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ScannerException("Cannot read from given file!");
        }
    }

    private void checkSyntax() throws ScannerException {

        checkParentheses(program);
    }

    public void parse() throws ScannerException {

        read();
        checkSyntax();
        generateInternalForm();
    }

    private void generateInternalForm() throws ScannerException {

        String[] program = this.program
                .replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ")
                .trim()
                .split("\\s+");

        for (String symbol: program) {
            try {
                internalForm.add(symbol, symbolTable);
            } catch (ProgramException e) {
                throw new ScannerException(e.getMessage());
            }
        }
    }

    private void checkParentheses(String program) throws ScannerException {

        Stack<Integer> stack = new Stack<>();

        for (int index = 0; index < program.length(); ++index) {
            if (program.charAt(index) == '(') {
                stack.push(index);
            }
            else if (program.charAt(index) == ')') {
                if (!stack.empty()) {
                    stack.pop();
                }
                else {
                    int startIndex = index - 20 >= 0 ? index - 20 : 0;
                    int endIndex = index;
                    throw new ScannerException("Extra closing bracket: " + program.substring(startIndex, endIndex) + "...");
                }
            }
        }

        StringBuffer error = new StringBuffer();
        for (int index: stack) {
            int startIndex = index;
            int endIndex = (index + 20 < program.length()) ? index + 20 : program.length();
            error.append("Extra opening bracket: " + program.substring(startIndex, endIndex) + "...\n");
        }

        if (error.length() > 0) {
            throw new ScannerException(error.toString());
        }
    }
}
