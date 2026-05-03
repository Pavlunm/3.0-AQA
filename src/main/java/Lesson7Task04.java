public final class Lesson7Task04 {

    private Lesson7Task04() {
    }

    /**
     * Сравнивает два целых числа.
     *
     * @return отрицательное значение, если {@code a < b}; ноль, если равны; положительное, если {@code a > b}
     */
    public static int compare(int a, int b) {
        return Integer.compare(a, b);
    }
}
