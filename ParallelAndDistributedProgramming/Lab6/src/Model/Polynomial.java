package Model;

import java.util.Arrays;

public class Polynomial {

    private int[] polynomial;
    private final int size;

    public Polynomial(int size) {
        this.polynomial = new int[size];
        this.size = size;

        for (int index = 0; index < this.size; ++index) {
            this.polynomial[index] = 0;
        }
    }

    public Polynomial(int[] polynomial) {
        this.polynomial = polynomial;
        this.size = polynomial.length;
    }

    public int get(int index) {
        return polynomial[index];
    }

    public void set(int index, int value) {
        polynomial[index] = value;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "Polynomial{" +
                "polynomial=" + Arrays.toString(polynomial) +
                ", size=" + size +
                '}';
    }
}
