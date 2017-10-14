package com.company;

import com.company.Interpreter.Interpreter;
import com.company.Interpreter.InterpreterException;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        String sourceCode;

        System.out.println("Enter the name of the source code file:");
        sourceCode = input.nextLine();

        Interpreter interpreter = new Interpreter(sourceCode);
        try {
            interpreter.interpret();
        } catch (InterpreterException e) {
            System.err.println(e.getMessage());
        }
    }
}
