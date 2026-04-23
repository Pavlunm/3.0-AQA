public class Lesson4Task01 {
    public static void main(String[] args) {
        Dog dog = new Dog("Rex");
        Cat cat1 = new Cat("Barsik", 15);
        Cat cat2 = new Cat("Murka", 20);
        Cat cat3 = new Cat("Luna", 12);

        System.out.println("Run and swim:");
        dog.run(300);
        dog.run(700);
        dog.swim(5);
        dog.swim(12);

        cat1.run(120);
        cat1.run(250);
        cat1.swim(1);

        System.out.println();
        Bowl bowl = new Bowl(20);
        Cat[] cats = {cat1, cat2, cat3};

        System.out.println("Bowl food before feeding: " + bowl.getFoodAmount());
        for (int i = 0; i < cats.length; i++) {
            cats[i].eat(bowl);
        }

        System.out.println("Cats satiety after first feeding:");
        for (int i = 0; i < cats.length; i++) {
            System.out.println(cats[i].getName() + ": " + cats[i].isSatiety());
        }

        System.out.println();
        bowl.addFood(30);
        System.out.println("Bowl food after refill: " + bowl.getFoodAmount());
        for (int i = 0; i < cats.length; i++) {
            cats[i].eat(bowl);
        }

        System.out.println("Cats satiety after second feeding:");
        for (int i = 0; i < cats.length; i++) {
            System.out.println(cats[i].getName() + ": " + cats[i].isSatiety());
        }
    }
}

class Animal {
    String name;
    int maxRunDistance;
    int maxSwimDistance;

    public Animal(String name, int maxRunDistance, int maxSwimDistance) {
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
    private int appetite;
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
            System.out.println(getName() + " ate " + appetite);
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
