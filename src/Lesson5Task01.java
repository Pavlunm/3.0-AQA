public class Lesson5Task01 {
    public static void main(String[] args) {
        String[][] goodArray = {
                {"1", "2", "3", "4"},
                {"5", "6", "7", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "15", "16"}
        };

        String[][] badDataArray = {
                {"1", "2", "3", "4"},
                {"5", "6", "oops", "8"},
                {"9", "10", "11", "12"},
                {"13", "14", "15", "16"}
        };

        String[][] badSizeArray = {
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", "9"}
        };

        try {
            int sum = sumArray(goodArray);
            System.out.println("Сумма goodArray: " + sum);
        } catch (MyArraySizeException | MyArrayDataException e) {
            System.out.println("Ошибка goodArray: " + e.getMessage());
        }

        try {
            int sum = sumArray(badDataArray);
            System.out.println("Сумма badDataArray: " + sum);
        } catch (MyArraySizeException | MyArrayDataException e) {
            System.out.println("Ошибка badDataArray: " + e.getMessage());
        }

        try {
            int sum = sumArray(badSizeArray);
            System.out.println("Сумма badSizeArray: " + sum);
        } catch (MyArraySizeException | MyArrayDataException e) {
            System.out.println("Ошибка badSizeArray: " + e.getMessage());
        }
    }

    public static int sumArray(String[][] array) throws MyArraySizeException, MyArrayDataException {
        // В этом задании ждем только массив 4x4
        if (array.length != 4) {
            throw new MyArraySizeException("Ожидается 4 строки, получено: " + array.length);
        }

        int sum = 0;
        for (int row = 0; row < 4; row++) {
            if (array[row].length != 4) {
                throw new MyArraySizeException(
                        "Ожидается 4 столбца в строке " + row + ", получено: " + array[row].length
                );
            }

            for (int col = 0; col < 4; col++) {
                try {
                    sum += Integer.parseInt(array[row][col]);
                } catch (NumberFormatException e) {
                    throw new MyArrayDataException(
                            "Некорректное значение в ячейке [" + row + "][" + col + "]: " + array[row][col]
                    );
                }
            }
        }

        return sum;
    }
}

class MyArraySizeException extends Exception {
    public MyArraySizeException(String message) {
        super(message);
    }
}

class MyArrayDataException extends Exception {
    public MyArrayDataException(String message) {
        super(message);
    }
}
