import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class Lesson7Task04Test {

    @Test
    public void lessThan() {
        assertTrue(Lesson7Task04.compare(1, 2) < 0);
    }

    @Test
    public void greaterThan() {
        assertTrue(Lesson7Task04.compare(3, 2) > 0);
    }

    @Test
    public void equal() {
        assertEquals(Lesson7Task04.compare(5, 5), 0);
    }
}
