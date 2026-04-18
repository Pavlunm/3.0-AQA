public class Lesson2Task6 {
    public static void main(String[] args) {
        printNumberSign(10);
        printNumberSign(-3);
        printNumberSign(0);
    }

    public static void printNumberSign(int number) {
        if (number >= 0) {
            System.out.println("Число положительное");
        } else {
            System.out.println("Число отрицательное");
        }
    }
}
