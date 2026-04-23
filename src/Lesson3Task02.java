public class Lesson3Task02 {
    public static void main(String[] args) {
        Product[] productsArray = new Product[5];

        productsArray[0] = new Product("Samsung S25 Ultra", "01.02.2025", "Samsung Corp.", "Korea", 5999.0, true);
        productsArray[1] = new Product("iPhone 16 Pro", "15.09.2025", "Apple Inc.", "USA", 6499.0, false);
        productsArray[2] = new Product("Xiaomi 15", "03.01.2025", "Xiaomi", "China", 4299.0, true);
        productsArray[3] = new Product("Google Pixel 10", "20.03.2025", "Google", "USA", 5199.0, false);
        productsArray[4] = new Product("Sony Xperia 1 VII", "07.04.2025", "Sony", "Japan", 4899.0, false);

        for (int i = 0; i < productsArray.length; i++) {
            productsArray[i].printInfo();
            System.out.println("-------------------------");
        }
    }
}
