import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class Lesson7Task01Test {

    @Test
    public void factorialOfZeroIsOne() {
        assertEquals(Lesson7Task01.factorial(0), 1L);
    }

    @Test
    public void factorialOfFive() {
        assertEquals(Lesson7Task01.factorial(5), 120L);
    }

    @Test
    public void factorialOfOne() {
        assertEquals(Lesson7Task01.factorial(1), 1L);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void factorialNegativeThrows() {
        Lesson7Task01.factorial(-1);
    }
}
