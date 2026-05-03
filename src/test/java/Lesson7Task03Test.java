import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class Lesson7Task03Test {

    @Test
    public void add() {
        assertEquals(Lesson7Task03.add(3, 4), 7);
    }

    @Test
    public void subtract() {
        assertEquals(Lesson7Task03.subtract(3, 4), -1);
    }

    @Test
    public void multiply() {
        assertEquals(Lesson7Task03.multiply(3, 4), 12);
    }

    @Test
    public void divide() {
        assertEquals(Lesson7Task03.divide(7, 3), 2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void divideByZeroThrows() {
        Lesson7Task03.divide(1, 0);
    }
}
