import models.Vehicle;
import models.Reservation;
import models.Customer;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer("John", "Doe", "DL12345678", "john.doe@example.com", "555-1234", false);        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        System.out.printf(customer.toString());
    }
}