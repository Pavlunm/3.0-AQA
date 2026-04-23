public class Lesson4Task01 {
    public static void main(String[] args) {
        Dog dog = new Dog("Rex");
        Cat cat1 = new Cat("Barsik", 15);
        Cat cat2 = new Cat("Murka", 20);
        Cat cat3 = new Cat("Luna", 12);

        System.out.println("Animals: " + Animal.count);
        System.out.println("Dogs: " + Dog.count);
        System.out.println("Cats: " + Cat.count);
        System.out.println();

        System.out.println("Run/Swim:");
        dog.run(300);
        dog.run(700);
        dog.swim(5);
        dog.swim(12);

        cat1.run(120);
        cat1.run(250);
        cat1.swim(1);

        System.out.println();

        Bowl bowl = new Bowl(20);
        System.out.println("Food in bowl: " + bowl.getFoodAmount());

        cat1.eat(bowl);
        cat2.eat(bowl);
        cat3.eat(bowl);

        System.out.println("Satiety 1:");
        System.out.println(cat1.name + ": " + cat1.isSatiety());
        System.out.println(cat2.name + ": " + cat2.isSatiety());
        System.out.println(cat3.name + ": " + cat3.isSatiety());

        System.out.println();

        bowl.addFood(30);
        System.out.println("Food after add: " + bowl.getFoodAmount());

        cat1.eat(bowl);
        cat2.eat(bowl);
        cat3.eat(bowl);

        System.out.println("Satiety 2:");
        System.out.println(cat1.name + ": " + cat1.isSatiety());
        System.out.println(cat2.name + ": " + cat2.isSatiety());
        System.out.println(cat3.name + ": " + cat3.isSatiety());
    }
}

class Animal {
    static int count = 0;
    public String name;
    int maxRunDistance;
    int maxSwimDistance;

    public Animal(String name, int maxRunDistance, int maxSwimDistance) {
        count++;
        this.name = name;
        this.maxRunDistance = maxRunDistance;
        this.maxSwimDistance = maxSwimDistance;
    }

    public void run(int distance) {
        if (distance <= maxRunDistance) {
            System.out.println(name + " run " + distance);
        } else {
            System.out.println(name + " no run " + distance);
        }
    }

    public void swim(int distance) {
        if (maxSwimDistance == 0) {
            System.out.println(name + " no swim");
            return;
        }

        if (distance <= maxSwimDistance) {
            System.out.println(name + " swim " + distance);
        } else {
            System.out.println(name + " no swim " + distance);
        }
    }
}

class Dog extends Animal {
    static int count = 0;

    public Dog(String name) {
        super(name, 500, 10);
        count++;
    }
}

class Cat extends Animal {
    static int count = 0;
    private int appetite;
    private boolean satiety;

    public Cat(String name, int appetite) {
        super(name, 200, 0);
        count++;
        this.appetite = appetite;
        this.satiety = false;
    }

    public boolean isSatiety() {
        return satiety;
    }

    public void eat(Bowl bowl) {
        if (satiety) {
            System.out.println(name + " full");
            return;
        }

        if (bowl.takeFood(appetite)) {
            satiety = true;
            System.out.println(name + " ate " + appetite);
        } else {
            System.out.println(name + " no food");
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
