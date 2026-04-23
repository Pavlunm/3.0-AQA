import java.util.ArrayList;
import java.util.List;

public class Park {
    private final String parkName;
    private final List<Attraction> attractions = new ArrayList<>();

    public Park(String parkName) {
        this.parkName = parkName;
    }

    public void addAttraction(String attractionName, String openingHours, double ticketPrice) {
        attractions.add(new Attraction(attractionName, openingHours, ticketPrice));
    }

    public void printAttractions() {
        System.out.println("Park: " + parkName);
        for (Attraction attraction : attractions) {
            attraction.printInfo();
            System.out.println("-------------------------");
        }
    }

    private class Attraction {
        private final String attractionName;
        private final String openingHours;
        private final double ticketPrice;

        public Attraction(String attractionName, String openingHours, double ticketPrice) {
            this.attractionName = attractionName;
            this.openingHours = openingHours;
            this.ticketPrice = ticketPrice;
        }

        public void printInfo() {
            System.out.println("Attraction: " + attractionName);
            System.out.println("Opening hours: " + openingHours);
            System.out.println("Ticket price: " + ticketPrice);
        }
    }
}
