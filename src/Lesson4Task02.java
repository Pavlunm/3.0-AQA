public class Lesson4Task02 {
    public static void main(String[] args) {
        Shape circle = new Circle("white", "black", 10);
        Shape rectangle = new Rectangle("yellow", "blue", 8, 5);
        Shape triangle = new Triangle("gray", "green", 3, 4, 5);

        System.out.println("CIRCLE");
        System.out.println("Perimeter: " + circle.perimeter());
        System.out.println("Area: " + circle.area());
        System.out.println("Fill color: " + circle.getFillColor());
        System.out.println("Border color: " + circle.getBorderColor());

        System.out.println("RECTANGLE");
        System.out.println("Perimeter: " + rectangle.perimeter());
        System.out.println("Area: " + rectangle.area());
        System.out.println("Fill color: " + rectangle.getFillColor());
        System.out.println("Border color: " + rectangle.getBorderColor());

        System.out.println("TRIANGLE");
        System.out.println("Perimeter: " + triangle.perimeter());
        System.out.println("Area: " + triangle.area());
        System.out.println("Fill color: " + triangle.getFillColor());
        System.out.println("Border color: " + triangle.getBorderColor());
    }
}

interface Shape {
    default double perimeter() {
        return 0;
    }

    default double area() {
        return 0;
    }

    String getFillColor();

    String getBorderColor();
}

class Circle implements Shape {
    private String fillColor;
    private String borderColor;
    private double radius;

    public Circle(String fillColor, String borderColor, double radius) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
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

    @Override
    public String getFillColor() {
        return fillColor;
    }

    @Override
    public String getBorderColor() {
        return borderColor;
    }
}

class Rectangle implements Shape {
    private String fillColor;
    private String borderColor;
    private double width;
    private double height;

    public Rectangle(String fillColor, String borderColor, double width, double height) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
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

    @Override
    public String getFillColor() {
        return fillColor;
    }

    @Override
    public String getBorderColor() {
        return borderColor;
    }
}

class Triangle implements Shape {
    private String fillColor;
    private String borderColor;
    private double a;
    private double b;
    private double c;

    public Triangle(String fillColor, String borderColor, double a, double b, double c) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
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

    @Override
    public String getFillColor() {
        return fillColor;
    }

    @Override
    public String getBorderColor() {
        return borderColor;
    }
}
