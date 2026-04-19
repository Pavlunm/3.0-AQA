public class Lesson2Task7 {
    public static void main(String[] args) {
        System.out.println(isNegative(-5));
        System.out.println(isNegative(3));
        System.out.println(isNegative(0));
    }
    public static boolean isNegative(int number) {
        return number < 0;
    }
}
