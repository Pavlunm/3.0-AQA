public class Lesson2Task5 {
    public static void main(String[] args) {
        System.out.println(checkSumInRange(4, 8));
        System.out.println(checkSumInRange(15, 10));
    }
    public static boolean checkSumInRange(int a, int b) {
        int sum = a + b;
        return sum >= 10 && sum <= 20;
    }
}