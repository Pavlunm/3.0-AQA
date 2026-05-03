import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class Lesson7Task02Test {

    @Test
    public void rightTriangle345() {
        assertEquals(Lesson7Task02.triangleArea(3, 4, 5), 6.0, 1e-9);
    }

    @Test
    public void equilateralTriangle() {
        double expected = Math.sqrt(3) / 4.0 * 4 * 4;
        assertEquals(Lesson7Task02.triangleArea(4, 4, 4), expected, 1e-9);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nonPositiveSideThrows() {
        Lesson7Task02.triangleArea(0, 1, 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void triangleInequalityThrows() {
        Lesson7Task02.triangleArea(1, 2, 10);
    }
}
