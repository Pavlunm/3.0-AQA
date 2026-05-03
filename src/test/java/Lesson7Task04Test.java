import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Lesson7Task04Test {

    @Test
    void lessThan() {
        assertTrue(Lesson7Task04.compare(1, 2) < 0);
    }

    @Test
    void greaterThan() {
        assertTrue(Lesson7Task04.compare(3, 2) > 0);
    }

    @Test
    void equal() {
        assertEquals(0, Lesson7Task04.compare(5, 5));
    }
}
