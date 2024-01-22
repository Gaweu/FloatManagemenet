package org.cassandraproject;

import com.datastax.driver.core.*;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.KeyspaceOptions;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.cassandraproject.exception.BackendException;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class CassandraService {
    private static PreparedStatement SELECT_ALL_FROM_USERS;
	private static PreparedStatement INSERT_INTO_USERS;
	private static PreparedStatement DELETE_ALL_FROM_USERS;
	private static final String USER_FORMAT = "- ID: %-10d name: %-16s";

    private final List<String> addresses = new ArrayList<>();
    private String selectedAddress;
    private Integer port;
    private String keySpace;
    private String passwordDB;
    private String usernameDB;

    private Session session;

    public CassandraService(Properties properties){
        log.debug("Initializing variables in CassandraService in "+Thread.currentThread().getName());
        initVariables(properties);
        this.selectedAddress = getRandomAddress();
        log.debug("Thread: "+Thread.currentThread().getName()+" picked: "+this.selectedAddress);

        try {
            Cluster cluster = Cluster.builder()
                .addContactPoint(this.selectedAddress)
                .withPort(this.port)
                .withCredentials(this.usernameDB, this.passwordDB)
                .build();

            log.debug("Trying to connect...");
            log.debug("Trying to connect to Cassandra cluster at " + selectedAddress);
            this.session = cluster.connect();
            log.debug("Connected to cluster");
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Failed connecting to cluster");
        }
    }

    public void initTables() throws BackendException {
        // Create tables first
            createTableUsers();
            createTableCarDealerships();
            createTableVehicles();
            createTableReservationRequests();
            createTableVehicleReservations();

    }

    private String getRandomAddress() {
        Random random = new Random();
        int index = random.nextInt(this.addresses.size());
        return this.addresses.get(index);
    }

    private void initVariables(Properties properties){
        String addressOne = System.getenv().getOrDefault("CASSANDRA_SERVER_ADDRESS_ONE", properties.getProperty("server.address_one"));
        String addressTwo = System.getenv().getOrDefault("CASSANDRA_SERVER_ADDRESS_TWO", properties.getProperty("server.address_two"));
        String addressThree = System.getenv().getOrDefault("CASSANDRA_SERVER_ADDRESS_THREE", properties.getProperty("server.address_three"));

        this.port = Integer.parseInt(System.getenv().getOrDefault("CASSANDRA_SERVER_PORT",properties.getProperty("server.port")));
        this.keySpace = System.getenv().getOrDefault("CASSANDRA_KEYSPACE",properties.getProperty("db.keyspace"));
        this.usernameDB = System.getenv().getOrDefault("CASSANDRA_USER",properties.getProperty("db.username"));
        this.passwordDB = System.getenv().getOrDefault("CASSANDRA_PASSWORD",properties.getProperty("db.password"));

        if(addressOne == null){
            System.out.println("ERROR INITIALIZING VARIABLE address");
            log.error("ERROR INITIALIZING VARIABLES address one");
            System.exit(1);
        }
        if(addressTwo == null){
            System.out.println("ERROR INITIALIZING VARIABLE address");
            log.error("ERROR INITIALIZING VARIABLES address two");
            System.exit(1);
        }
        if(addressThree == null){
            System.out.println("ERROR INITIALIZING VARIABLE address");
            log.error("ERROR INITIALIZING VARIABLES address three");
            System.exit(1);
        }

        this.addresses.add(addressOne);
        this.addresses.add(addressTwo);
        this.addresses.add(addressThree);

        if(this.port == null){
            System.out.println("ERROR INITIALIZING VARIABLE port");
            log.error("ERROR INITIALIZING VARIABLES port");
            System.exit(1);
        }
        if(this.keySpace == null){
            System.out.println("ERROR INITIALIZING VARIABLE keySpace");
            log.error("ERROR INITIALIZING VARIABLES keySpace");
            System.exit(1);
        }
        if(this.usernameDB == null){
            System.out.println("ERROR INITIALIZING VARIABLE usernameDB");
            log.error("ERROR INITIALIZING VARIABLES usernameDB");
            System.exit(1);
        }
        if(this.passwordDB == null){
            System.out.println("ERROR INITIALIZING VARIABLE passwordDB");
            log.error("ERROR INITIALIZING VARIABLES passwordDB");
            System.exit(1);
        }

//        System.out.println(this.addresses);
//        System.out.println(this.port);
//        System.out.println(this.keySpace);
//        System.out.println(this.usernameDB);
//        System.out.println(this.passwordDB);

    }
    public void prepareStatements() throws BackendException {

        try{
            SELECT_ALL_FROM_USERS = session.prepare("SELECT * FROM users;");
            INSERT_INTO_USERS = session.prepare("INSERT INTO users (id,name) VALUES (?, ?);");
			DELETE_ALL_FROM_USERS = session.prepare("TRUNCATE users;");
            log.debug("Prepared statements");
        }catch (Exception e){
            throw new BackendException("Could not prepare statements. "+e.getMessage(),e);
        }
    }

    public void createKeySpace() throws BackendException {
        KeyspaceOptions keyspaceOptions = SchemaBuilder.createKeyspace(this.keySpace)
                .ifNotExists()
                .with()
                .replication(Map.of("class","SimpleStrategy", "replication_factor",3));
        keyspaceOptions.setConsistencyLevel(ConsistencyLevel.QUORUM);

        try{
            session.execute(keyspaceOptions);
            log.debug("Keyspace created successful");
        }catch (Exception e){
            log.error("creation of keyspace failed! "+e.getMessage());
            throw new BackendException("creation of keyspace failed! "+e.getMessage(),e);
        }
    }

    public void useKeyspace() {
        session.execute("use " + this.keySpace + ";");
        log.debug("Keyspace switched successful");
    }

    public void createTableUsers() {
        Create create = SchemaBuilder.createTable(this.keySpace, "users")
                .ifNotExists()
                .addPartitionKey("id", DataType.bigint())
                .addColumn("name", DataType.varchar());
        session.execute(create);
        log.info("Table users created successful");
    }


    public void createTableCarDealerships(){
        Create create = SchemaBuilder.createTable(this.keySpace, "dealerships")
                .ifNotExists()
                .addPartitionKey("id", DataType.bigint())
                .addColumn("name",DataType.varchar());
        session.execute(create);
        log.info("Table car dealerships created successful");
    }

    public void createTableVehicles(){
        Create create = SchemaBuilder.createTable(this.keySpace, "vehicles")
                .ifNotExists()
                .addPartitionKey("id", DataType.bigint())
                .addColumn("name",DataType.varchar())
                .addColumn("reservation_start_time", DataType.timestamp())
                .addColumn("reservation_end_time", DataType.timestamp())
                .addPartitionKey("dealership_id", DataType.bigint());
        session.execute(create);
        log.info("Table seats created successful");
    }

    public void seedVehicles(int numberOfVehicles) throws BackendException {
        for (int i = 1; i <= numberOfVehicles; i++) {
            upsertVehicle(BigInteger.valueOf(i), "Vehicle" + i, LocalDateTime.now(), LocalDateTime.now().plusHours(1), BigInteger.valueOf(i));
        }
        log.info(numberOfVehicles + " vehicles seeded.");
    }

    public void upsertVehicle(BigInteger id, String name, LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, BigInteger dealershipId) throws BackendException {
        BoundStatement bs = session.prepare("INSERT INTO vehicles (id, name, reservation_start_time, reservation_end_time, dealership_id) " +
                "VALUES (?, ?, ?, ?, ?);").bind(id.longValue(), name, Timestamp.valueOf(reservationStartTime),
                Timestamp.valueOf(reservationEndTime), dealershipId.longValue());

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an upsert for vehicle. " + e.getMessage(), e);
        }

        log.info("Vehicle " + name + " upserted");
    }
    public void createTableVehicleReservations(){
        Create create = SchemaBuilder.createTable(this.keySpace, "vehicle_reservations")
                .ifNotExists()
                .addPartitionKey("vehicle_id", DataType.bigint())
                .addClusteringColumn("user_id", DataType.bigint());
        session.execute(create);

        log.info("Table vehicle reservations created successful");
    }

    public void upsertUser(BigInteger id, String name) throws BackendException {
        BoundStatement bs = new BoundStatement(INSERT_INTO_USERS);

        // Convert BigInteger to Long
        Long longId = id.longValue();

        bs.bind(longId, name);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an upsert. " + e.getMessage() + ".", e);
        }

        log.info("User " + name + " upserted");
    }

    public void seedUsers(int numberOfUsers) throws BackendException {
        for (int i = 1; i <= numberOfUsers; i++) {
            upsertUser(BigInteger.valueOf(i), "User" + i);
        }
        log.info(numberOfUsers + " users seeded.");
    }

    public void seedCarDealerships(int numberOfCarDealerships) throws BackendException {
        for (int i = 1; i <= numberOfCarDealerships; i++) {
            upsertCarDealership(BigInteger.valueOf(i), "Dealership" + i);
        }
        log.info(numberOfCarDealerships + " car dealerships seeded.");
    }

    public void upsertCarDealership(BigInteger id, String name) throws BackendException {
        BoundStatement bs = session.prepare("INSERT INTO dealerships (id, name) VALUES (?, ?);").bind(id.longValue(), name);

        try {
            session.execute(bs);
        } catch (Exception e) {
            throw new BackendException("Could not perform an upsert for car dealership. " + e.getMessage(), e);
        }

        log.info("Car dealership " + name + " upserted");
    }

    public void createTableReservationRequests() {
        Create create = SchemaBuilder.createTable(this.keySpace, "reservation_requests")
                .ifNotExists()
                .addPartitionKey("vehicle_id", DataType.bigint())
                .addClusteringColumn("user_id", DataType.bigint())
                .addColumn("reservation_start", DataType.timestamp())
                .addColumn("reservation_duration", DataType.bigint())
                .addColumn( "request_time", DataType.timestamp());
        session.execute(create);
        log.info("Table reservation_requests created successful");
    }

    public void requestSeatReservation(long vehicleId, long userId, LocalDateTime reservationStart, long reservationDuration) throws BackendException {
        try {
            session.execute("INSERT INTO reservation_requests (vehicle_id, user_id, reservation_start, reservation_duration, request_time) VALUES (?, ?, ?, ?, dateof(now()));", vehicleId, userId, reservationStart, reservationDuration);
            log.info("[*** Reservation requested for user " + userId + " in vehicle " + vehicleId + " ***]");
        } catch (Exception e) {
            throw new BackendException("Error requesting reservation: " + e.getMessage(), e);
        }
    }


    public void processReservationRequests() throws BackendException {
        try {
            ResultSet reservationRequests = session.execute("SELECT * FROM reservation_requests;");

            for (Row request : reservationRequests) {
                long vehicleId = request.getLong("vehicle_id");
                long userId = request.getLong("user_id");
                Date reservationStartDate = request.getTimestamp("reservation_start");
                LocalDateTime reservationStart = reservationStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                long reservationDuration = request.getLong("reservation_duration");

                if (isVehicleAvailable(vehicleId, reservationStart, reservationDuration)) {
                    assignVehicleToUser(vehicleId, userId, reservationStart, reservationDuration);
                    log.info("[*** Vehicle " + vehicleId + " assigned to user " + userId + " for reservation ***]");
                } else {
                    log.info("[*** Vehicle " + vehicleId + " is not available for reservation at the specified time ***]");
                }
            }
        } catch (Exception e) {
            throw new BackendException("Error processing reservation requests: " + e.getMessage(), e);
        }
    }

    private boolean isVehicleAvailable(long vehicleId, LocalDateTime reservationStart, long reservationDuration) {
        try {
            ResultSet overlappingReservations = session.execute("SELECT * FROM vehicle_reservations WHERE vehicle_id = ? " +
                            "AND ((reservation_start_time >= ? AND reservation_start_time < ?) OR " +
                            "(reservation_end_time > ? AND reservation_end_time <= ?));",
                    vehicleId, reservationStart, reservationStart.plusSeconds(reservationDuration),
                    reservationStart, reservationStart.plusSeconds(reservationDuration));

            return overlappingReservations.isExhausted();
        } catch (Exception e) {
            log.error("Error checking vehicle availability: " + e.getMessage());
            return false;
        }
    }

    private void assignVehicleToUser(long vehicleId, long userId, LocalDateTime reservationStart, long reservationDuration) {
        try {
            session.execute("INSERT INTO vehicle_reservations (vehicle_id, user_id, reservation_start_time, reservation_end_time) " +
                            "VALUES (?, ?, ?, ?);", vehicleId, userId, Timestamp.valueOf(reservationStart),
                    Timestamp.valueOf(reservationStart.plusSeconds(reservationDuration)));
            session.execute("DELETE FROM reservation_requests WHERE vehicle_id = ? AND user_id = ?;", vehicleId, userId);
        } catch (Exception e) {
            log.error("Error assigning vehicle to user: " + e.getMessage());
        }
    }


    protected void finalize() {
		try {
			if (session != null) {
				session.getCluster().close();
			}
		} catch (Exception e) {
			log.error("Could not close existing cluster", e);
		}
	}
}
