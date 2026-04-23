public class Lesson3Task03 {
    public static void main(String[] args) {
        Park park = new Park("Central Park");
        park.addAttraction("Roller Coaster", "09:00-21:00", 15.0);
        park.addAttraction("Ferris Wheel", "10:00-22:00", 8.5);
        park.addAttraction("Bumper Cars", "11:00-20:00", 6.0);
        park.printAttractions();
    }
}
