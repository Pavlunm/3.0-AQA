import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Lesson7Task01Test {

    @Test
    void factorialOfZeroIsOne() {
        assertEquals(1, Lesson7Task01.factorial(0));
    }

    @Test
    void factorialOfFive() {
        assertEquals(120, Lesson7Task01.factorial(5));
    }

    @Test
    void factorialOfOne() {
        assertEquals(1, Lesson7Task01.factorial(1));
    }

    @Test
    void factorialNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> Lesson7Task01.factorial(-1));
    }
}
