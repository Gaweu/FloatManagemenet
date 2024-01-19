import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.UUID;
import java.time.LocalDateTime;
public class BackendSession {
    private Cluster cluster;
    private Session session;

    private PreparedStatement INSERT_INTO_CUSTOMERS;
    private PreparedStatement DELETE_FROM_CUSTOMERS;
    private PreparedStatement INSERT_INTO_VEHICLES;
    private PreparedStatement DELETE_FROM_VEHICLES;
    private PreparedStatement INSERT_INTO_RESERVATIONS;
    private PreparedStatement DELETE_FROM_RESERVATIONS;

    public void close() {
        session.close();
        cluster.close();
    }

    private void prepareStatements() {
        // Prepare statements for Customer
        INSERT_INTO_CUSTOMERS = session.prepare(
                "INSERT INTO Customers (customerID, firstName, lastName, driverLicenseNumber, email, phone, hasReservedCar) VALUES (?, ?, ?, ?, ?, ?, ?);"
        );
        DELETE_FROM_CUSTOMERS = session.prepare("DELETE FROM Customers WHERE customerID=?;");

        // Prepare statements for Vehicle
        INSERT_INTO_VEHICLES = session.prepare(
                "INSERT INTO Vehicles (vehicleID, brand, model, yearOfProduction, fuelType, mileage, isAvailable) VALUES (?, ?, ?, ?, ?, ?, ?);"
        );
        DELETE_FROM_VEHICLES = session.prepare("DELETE FROM Vehicles WHERE vehicleID=?;");

        // Prepare statements for Reservation
        INSERT_INTO_RESERVATIONS = session.prepare(
                "INSERT INTO Reservations (reservationID, customerID, vehicleID, startDate, scheduledEndDate, returnDate, isDelayed, delayTime, costs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );
        DELETE_FROM_RESERVATIONS = session.prepare("DELETE FROM Reservations WHERE reservationID=?;");
    }

    public void insertCustomer(UUID customerID, String firstName, String lastName, String driverLicenseNumber, String email, String phone, boolean hasReservedCar) {
        BoundStatement bs = new BoundStatement(INSERT_INTO_CUSTOMERS);
        bs.bind(customerID, firstName, lastName, driverLicenseNumber, email, phone, hasReservedCar);
        try {
            session.execute(bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteCustomer(UUID customerID) {
        BoundStatement bs = new BoundStatement(DELETE_FROM_CUSTOMERS);
        bs.bind(customerID);
        try {
            session.execute(bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertVehicle(UUID vehicleID, String brand, String model, int yearOfProduction, String fuelType, int mileage, boolean isAvailable) {
        BoundStatement bs = new BoundStatement(INSERT_INTO_VEHICLES);
        bs.bind(vehicleID, brand, model, yearOfProduction, fuelType, mileage, isAvailable);
        try {
            session.execute(bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteVehicle(UUID vehicleID) {
        BoundStatement bs = new BoundStatement(DELETE_FROM_VEHICLES);
        bs.bind(vehicleID);
        try {
            session.execute(bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertReservation(UUID reservationID, UUID customerID, UUID vehicleID, LocalDateTime startDate, LocalDateTime scheduledEndDate, LocalDateTime returnDate, boolean isDelayed, long delayTime, double costs) {
        BoundStatement bs = new BoundStatement(INSERT_INTO_RESERVATIONS);
        bs.bind(reservationID, customerID, vehicleID, startDate, scheduledEndDate, returnDate, isDelayed, delayTime, costs);
        try {
            session.execute(bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteReservation(UUID reservationID) {
        BoundStatement bs = new BoundStatement(DELETE_FROM_RESERVATIONS);
        bs.bind(reservationID);
        try {
            session.execute(bs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public BackendSession(String contactPoint, String keyspace) {
        cluster = Cluster.builder().addContactPoint(contactPoint).build();
        try {
            session = cluster.connect(keyspace);
            System.out.println("Connected to cluster: " + cluster.getClusterName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        prepareStatements();
    }
}
