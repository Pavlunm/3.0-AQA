import java.util.ArrayList;
import java.util.List;

public class Lesson4Task01 {
    public static void main(String[] args) {
        Dog dog = new Dog("Rex");
        Cat cat1 = new Cat("Barsik", 15);
        Cat cat2 = new Cat("Murka", 20);
        Cat cat3 = new Cat("Luna", 12);

        dog.run(300);
        dog.run(700);
        dog.swim(5);
        dog.swim(12);

        cat1.run(120);
        cat1.run(250);
        cat1.swim(1);

        Bowl bowl = new Bowl(20);
        List<Cat> cats = new ArrayList<>();
        cats.add(cat1);
        cats.add(cat2);
        cats.add(cat3);

        System.out.println("Bowl food before feeding: " + bowl.getFoodAmount());
        feedAllCats(cats, bowl);
        printCatsSatiety(cats);

        bowl.addFood(30);
        System.out.println("Bowl food after refill: " + bowl.getFoodAmount());
        feedAllCats(cats, bowl);
        printCatsSatiety(cats);
    }

    private static void feedAllCats(List<Cat> cats, Bowl bowl) {
        for (Cat cat : cats) {
            cat.eat(bowl);
        }
    }

    private static void printCatsSatiety(List<Cat> cats) {
        for (Cat cat : cats) {
            System.out.println(cat.getName() + " satiety: " + cat.isSatiety());
        }
    }
}

abstract class Animal {
    private final String name;
    private final int maxRunDistance;
    private final int maxSwimDistance;

    protected Animal(String name, int maxRunDistance, int maxSwimDistance) {
        this.name = name;
        this.maxRunDistance = maxRunDistance;
        this.maxSwimDistance = maxSwimDistance;
    }

    public String getName() {
        return name;
    }

    public void run(int distance) {
        if (distance <= maxRunDistance) {
            System.out.println(name + " runs " + distance + " m");
        } else {
            System.out.println(name + " cannot run " + distance + " m (max " + maxRunDistance + ")");
        }
    }

    public void swim(int distance) {
        if (maxSwimDistance == 0) {
            System.out.println(name + " cannot swim");
            return;
        }

        if (distance <= maxSwimDistance) {
            System.out.println(name + " swims " + distance + " m");
        } else {
            System.out.println(name + " cannot swim " + distance + " m (max " + maxSwimDistance + ")");
        }
    }
}

class Dog extends Animal {
    public Dog(String name) {
        super(name, 500, 10);
    }
}

class Cat extends Animal {
    private final int appetite;
    private boolean satiety;

    public Cat(String name, int appetite) {
        super(name, 200, 0);
        this.appetite = appetite;
        this.satiety = false;
    }

    public boolean isSatiety() {
        return satiety;
    }

    public void eat(Bowl bowl) {
        if (satiety) {
            System.out.println(getName() + " is already full");
            return;
        }

        if (bowl.takeFood(appetite)) {
            satiety = true;
            System.out.println(getName() + " ate " + appetite + " food");
        } else {
            System.out.println(getName() + " cannot eat, not enough food");
        }
    }
}

class Bowl {
    private int foodAmount;

    public Bowl(int foodAmount) {
        this.foodAmount = foodAmount;
    }

    public int getFoodAmount() {
        return foodAmount;
    }

    public boolean takeFood(int amount) {
        if (amount <= foodAmount) {
            foodAmount -= amount;
            return true;
        }
        return false;
    }

    public void addFood(int amount) {
        if (amount > 0) {
            foodAmount += amount;
        }
    }
}
