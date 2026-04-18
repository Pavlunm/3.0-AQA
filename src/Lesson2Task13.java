public class Lesson2Task13 {
    public static void main(String[] args) {
        int size = 5;
        int[][] array = new int[size][size];
        fillMainDiagonal(array);
        print2DArray(array);
    }

    public static void fillMainDiagonal(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            array[i][i] = 1;
        }
    }

    public static void print2DArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }
}
