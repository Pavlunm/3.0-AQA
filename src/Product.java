public class Product {
    private final String name;
    private final String productionDate;
    private final String manufacturer;
    private final String countryOfOrigin;
    private final double price;
    private final boolean isReserved;

    public Product(
            String name,
            String productionDate,
            String manufacturer,
            String countryOfOrigin,
            double price,
            boolean isReserved
    ) {
        this.name = name;
        this.productionDate = productionDate;
        this.manufacturer = manufacturer;
        this.countryOfOrigin = countryOfOrigin;
        this.price = price;
        this.isReserved = isReserved;
    }

    public void printInfo() {
        System.out.println("Name: " + name);
        System.out.println("Production date: " + productionDate);
        System.out.println("Manufacturer: " + manufacturer);
        System.out.println("Country of origin: " + countryOfOrigin);
        System.out.println("Price: " + price);
        System.out.println("Reserved by customer: " + isReserved);
    }
}
