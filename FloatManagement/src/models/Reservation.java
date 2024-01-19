package models;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Reservation {
    private UUID reservationID;
    private Customer customer;
    private Vehicle vehicle;
    private LocalDateTime startDate;
    private LocalDateTime scheduledEndDate;
    private LocalDateTime returnDate;
    private boolean isDelayed;
    private long delayTime;
    private double costs;

    private static final double COST_PER_MINUTE_DELAY = 1.0;
    private static final double BASE_COST = 100.0; // Base cost for rental

    public Reservation(Customer customer, Vehicle vehicle, LocalDateTime startDate, LocalDateTime scheduledEndDate) {
        this.reservationID = UUID.randomUUID();
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.scheduledEndDate = scheduledEndDate.plusMinutes(30); // Add 30 minutes for cleaning
        this.returnDate = null;
        this.isDelayed = false;
        this.delayTime = 0;
        this.costs = BASE_COST; // initial cost is the base cost
    }

    // Method to return the vehicle
    public void returnVehicle(LocalDateTime returnDate) {
        this.returnDate = returnDate;
        calculateDelay();
        calculateCosts();
    }

    // Calculate if the return is delayed and by how much
    private void calculateDelay() {
        if (returnDate.isAfter(scheduledEndDate)) {
            isDelayed = true;
            delayTime = ChronoUnit.MINUTES.between(scheduledEndDate, returnDate);
        } else {
            isDelayed = false;
            delayTime = 0;
        }
    }

    // Calculate the total cost of the reservation
    private void calculateCosts() {
        if (isDelayed) {
            costs += delayTime * COST_PER_MINUTE_DELAY; // Add delay costs
        }
        //... add other costs calculations as needed
    }

    // Getters and Setters (Add as necessary for your use case)

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getScheduledEndDate() {
        return scheduledEndDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public boolean isDelayed(){
        return isDelayed;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public double getCosts() {
        return costs;
    }
}
