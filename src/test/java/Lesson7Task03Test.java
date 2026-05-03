import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Lesson7Task03Test {

    @Test
    void add() {
        assertEquals(7, Lesson7Task03.add(3, 4));
    }

    @Test
    void subtract() {
        assertEquals(-1, Lesson7Task03.subtract(3, 4));
    }

    @Test
    void multiply() {
        assertEquals(12, Lesson7Task03.multiply(3, 4));
    }

    @Test
    void divide() {
        assertEquals(2, Lesson7Task03.divide(7, 3));
    }

    @Test
    void divideByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> Lesson7Task03.divide(1, 0));
    }
}
