package models;
import java.util.UUID;

public class Customer {
    private UUID customerID;
    private String firstName;
    private String lastName;
    private String driverLicenseNumber;
    private String email;
    private String phone;
    private boolean hasReservedCar;

    public Customer(String firstName, String lastName, String driverLicenseNumber, String email, String phone, boolean hasReservedCar) {
        this.customerID = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.driverLicenseNumber = driverLicenseNumber;
        this.email = email;
        this.phone = phone;
        this.hasReservedCar = hasReservedCar;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean hasReservedCar() {
        return hasReservedCar;
    }

    public void setHasReservedCar(boolean hasReservedCar) {
        this.hasReservedCar = hasReservedCar;
    }

    @Override
    public String toString() {
        return "CustomerDetails{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", driverLicenseNumber='" + driverLicenseNumber + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", hasReservedCar=" + hasReservedCar +
                '}';
    }
}
