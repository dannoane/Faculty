package Model;

public class Matrix {

    int[][] matrix;
    int rows, cols;

    public Matrix(int rows, int cols) {

        this.matrix = new int[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int get(int row, int col) {
        return matrix[row][col];
    }

    public void set(int row, int col, int value) {
        matrix[row][col] = value;
    }

    public int size() {
        return rows * cols;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();

        for (int[] row: matrix) {
            for (int item: row) {
                buffer.append(item + " ");
            }
            buffer.append("\n");
        }

        return buffer.toString();
    }
}
