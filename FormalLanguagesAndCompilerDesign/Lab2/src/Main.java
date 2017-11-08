import FiniteAutomatonScanner.FiniteAutomatonFileScanner;
import FiniteAutomatonScanner.FiniteAutomatonKeyboardScanner;
import FiniteAutomatonScanner.FiniteAutomatonScannerException;
import FiniteAutomatonScanner.IFiniteAutomatonScanner;
import Interpreter.FiniteAutomaton.IFiniteStateMachine;

public class Main {

    public static void main(String[] args) {

//        Scanner input = new Scanner(System.in);
//        String sourceCode;
//
//        System.out.println("Enter the name of the source code file:");
//        sourceCode = input.nextLine();
//
//        Interpreter interpreter = new Interpreter(sourceCode);
//        try {
//            interpreter.interpret();
//        } catch (InterpreterException e) {
//            System.err.println(e.getMessage());
//        }

        IFiniteAutomatonScanner faScanner = new FiniteAutomatonFileScanner("integer_automaton.json");
        IFiniteStateMachine fa = null;
        try {
            fa = faScanner.read();
        } catch (FiniteAutomatonScannerException e) {
            e.printStackTrace();
        }

        fa.print();

        String number = "123.023";
        for (int i = 0; i < number.length(); ++i) {
            fa = fa.switchCase(String.valueOf(number.charAt(i)));
        }

        assert fa.canStop();
    }
}
