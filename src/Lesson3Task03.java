public class Lesson3Task03 {
    public static void main(String[] args) {
        Park park = new Park("Парк Челюскинцев (Минск)");
        park.addAttraction("Колесо обозрения", "10:00-22:00", 8.5);
        park.addAttraction("Американские горки", "11:00-21:00", 12.0);
        park.addAttraction("Автодром", "10:00-20:00", 6.0);
        park.printAttractions();
    }
}
