public class Lesson2Task8 {
    public static void main(String[] args) {
        printStringNTimes("Привет!", 3);
    }

    public static void printStringNTimes(String text, int count) {
        for (int i = 0; i < count; i++) {
            System.out.println(text);
        }
    }
}
