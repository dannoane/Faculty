package FiniteAutomatonScanner;

import Interpreter.FiniteAutomaton.IFiniteStateMachine;

public interface IFiniteAutomatonScanner {

    IFiniteStateMachine read() throws FiniteAutomatonScannerException;
}
