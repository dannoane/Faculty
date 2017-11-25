import java.util.ArrayList;
import java.util.List;

public class BigNumber {

    private List<Integer> number;

    public BigNumber() {

        this.number = new ArrayList<>();
    }

    int getDigit(int index) {
        return number.get(index);
    }

    void setDigit(int index, int value) {

        if (index > (number.size() - 1)) {
            number.add(value);
        }
        else {
            number.set(index, value);
        }
    }

    int length() {
        return number.size();
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();

        for (int index = number.size() - 1; index >= 0; --index) {
            buffer.append(number.get(index));
        }

        return buffer.toString();
    }
}
