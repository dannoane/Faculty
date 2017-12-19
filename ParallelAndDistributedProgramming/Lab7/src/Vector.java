
public class Vector {

    private Cell[] vector;

    public Vector(int length) {

        this.vector = new Cell[length];
        for (int i = 0; i < length; ++i) {
            this.vector[i] = new Cell();
        }
    }

    public int length() {
        return this.vector.length;
    }

    public int get(int index) throws InterruptedException {
        return this.vector[index].get();
    }

    public void set(int index, int value) throws InterruptedException {
        this.vector[index].set(value);
    }
}
