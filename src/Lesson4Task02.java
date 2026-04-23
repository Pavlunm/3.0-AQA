import java.util.List;

public class Lesson4Task02 {
    public static void main(String[] args) {
        List<Shape> shapes = List.of(
                new Circle("white", "black", 10),
                new Rectangle("yellow", "blue", 8, 5),
                new Triangle("gray", "green", 3, 4, 5)
        );

        for (Shape shape : shapes) {
            System.out.println(
                    "Perimeter=" + round(shape.perimeter())
                            + ", Area=" + round(shape.area())
                            + ", FillColor=" + shape.getFillColor()
                            + ", BorderColor=" + shape.getBorderColor()
            );
        }
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

interface Shape {
    double perimeter();

    double area();

    String getFillColor();

    String getBorderColor();
}

abstract class BaseShape implements Shape {
    private final String fillColor;
    private final String borderColor;

    protected BaseShape(String fillColor, String borderColor) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
    }

    @Override
    public String getFillColor() {
        return fillColor;
    }

    @Override
    public String getBorderColor() {
        return borderColor;
    }
}

class Circle extends BaseShape {
    private final double radius;

    public Circle(String fillColor, String borderColor, double radius) {
        super(fillColor, borderColor);
        this.radius = radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends BaseShape {
    private final double width;
    private final double height;

    public Rectangle(String fillColor, String borderColor, double width, double height) {
        super(fillColor, borderColor);
        this.width = width;
        this.height = height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public double area() {
        return width * height;
    }
}

class Triangle extends BaseShape {
    private final double a;
    private final double b;
    private final double c;

    public Triangle(String fillColor, String borderColor, double a, double b, double c) {
        super(fillColor, borderColor);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double perimeter() {
        return a + b + c;
    }

    @Override
    public double area() {
        double p = perimeter() / 2.0;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }
}
