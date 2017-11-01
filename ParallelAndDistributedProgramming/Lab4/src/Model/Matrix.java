package Model;

public class Matrix {

    Cell[][] matrix;
    int rows, cols;

    public Matrix(int rows, int cols) {

        this.matrix = new Cell[rows][cols];
        this.rows = rows;
        this.cols = cols;

        initMatrix();
    }

    private void initMatrix() {

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                matrix[row][col] = new Cell();
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int get(int row, int col) throws InterruptedException {
        return matrix[row][col].get();
    }

    public void set(int row, int col, int value) throws InterruptedException {
        matrix[row][col].set(value);
    }

    public int size() {
        return rows * cols;
    }

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();

        for (Cell[] row: matrix) {
            for (Cell item: row) {
                buffer.append(item + " ");
            }
            buffer.append("\n");
        }

        return buffer.toString();
    }
}
