import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Lesson7Task02Test {

    @Test
    void rightTriangle345() {
        assertEquals(6.0, Lesson7Task02.triangleArea(3, 4, 5), 1e-9);
    }

    @Test
    void equilateralTriangle() {
        double expected = Math.sqrt(3) / 4.0 * 4 * 4;
        assertEquals(expected, Lesson7Task02.triangleArea(4, 4, 4), 1e-9);
    }

    @Test
    void nonPositiveSideThrows() {
        assertThrows(IllegalArgumentException.class, () -> Lesson7Task02.triangleArea(0, 1, 1));
    }

    @Test
    void triangleInequalityThrows() {
        assertThrows(IllegalArgumentException.class, () -> Lesson7Task02.triangleArea(1, 2, 10));
    }
}
