public final class Lesson7Task01 {

    private Lesson7Task01() {
    }

    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Факториал определён только для неотрицательных чисел");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
