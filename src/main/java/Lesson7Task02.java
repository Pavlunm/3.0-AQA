public final class Lesson7Task02 {

    private Lesson7Task02() {
    }

    /**
     * Площадь треугольника по трём сторонам (формула Герона).
     */
    public static double triangleArea(double a, double b, double c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new IllegalArgumentException("Длины сторон должны быть положительными");
        }
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new IllegalArgumentException("Нарушено неравенство треугольника");
        }
        double s = (a + b + c) / 2.0;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}
