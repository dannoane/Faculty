package Program.FiniteAutomaton;

public class FAValidator {

    public static boolean isAccepted(IFiniteStateMachine fa, String sequence, StringBuffer acceptedSequence, int index) {

        if (fa.canStop()) {
            acceptedSequence.replace(0, acceptedSequence.length(), "");
            acceptedSequence.append(sequence.substring(0, index));
        }

        if (sequence.length() == index) {
            return fa.canStop();
        }

        try {
            return isAccepted(fa.switchCase(String.valueOf(sequence.charAt(index))), sequence, acceptedSequence, index + 1);
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
