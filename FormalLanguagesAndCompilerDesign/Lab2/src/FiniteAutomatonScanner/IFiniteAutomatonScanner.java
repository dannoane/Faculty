package FiniteAutomatonScanner;

import Program.FiniteAutomaton.IFiniteStateMachine;

public interface IFiniteAutomatonScanner {

    IFiniteStateMachine read() throws FiniteAutomatonScannerException;
}
