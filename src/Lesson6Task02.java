import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson6Task02 {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();

        phoneBook.add("Иванов", "+375-29-111-22-33");
        phoneBook.add("Петров", "+375-33-222-33-44");
        phoneBook.add("Иванов", "+375-44-444-55-66");

        System.out.println("Телефоны Ивановых: " + phoneBook.get("Иванов"));
        System.out.println("Телефоны Петровых: " + phoneBook.get("Петров"));
        System.out.println("Нет в справочнике: " + phoneBook.get("Сидоров"));
    }
}

class PhoneBook {

    private final Map<String, List<String>> lastNameToPhones = new HashMap<>();

    public void add(String lastName, String phoneNumber) {
        lastNameToPhones.computeIfAbsent(lastName, key -> new ArrayList<>()).add(phoneNumber);
    }

    public List<String> get(String lastName) {
        List<String> phones = lastNameToPhones.get(lastName);
        if (phones == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(phones));
    }
}
