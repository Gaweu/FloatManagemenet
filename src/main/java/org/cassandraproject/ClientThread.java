package org.cassandraproject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.Random;

@Slf4j
@AllArgsConstructor
public class ClientThread implements Runnable {

    private Properties properties;
    int numUsers;
    int numSectors;
    int numSeatsPerSectors;
    int numMatches;

    public ClientThread(Properties properties) {
        this.properties = properties;
        this.numUsers = Integer.parseInt(System.getenv().getOrDefault("ENV_USERS",properties.getProperty("stadium.num_users")));
        this.numSectors = Integer.parseInt(System.getenv().getOrDefault("ENV_NUM_SECTORS",properties.getProperty("stadium.num_sectors")));
        this.numSeatsPerSectors = Integer.parseInt(System.getenv().getOrDefault("ENV_NUM_SEATS_SECTOR",properties.getProperty("stadium.num_seats_per_sector")));
        this.numMatches = Integer.parseInt(System.getenv().getOrDefault("ENV_NUM_MATCHES",properties.getProperty("stadium.num_matches")));
    }

    private static LocalDateTime generateRandomTimestamp(LocalDateTime start, LocalDateTime end) {
        // Calculate the difference in seconds between start and end
        long secondsBetween = ChronoUnit.SECONDS.between(start, end);

        // Generate a random number of seconds within the range
        long randomSeconds = new Random().nextLong() % secondsBetween;

        // Adjust the random seconds to be non-negative
        randomSeconds = (randomSeconds + secondsBetween) % secondsBetween;

        // Add the random seconds to the start time to get the random timestamp
        return start.plusSeconds(randomSeconds);
    }
    @Override
    public void run() {
        log.info(Thread.currentThread().getName());
        try {
            log.debug("Trying to declare cassandraService in " + Thread.currentThread().getName());
            CassandraService cassandraService = new CassandraService(properties);
            cassandraService.useKeyspace();
            cassandraService.prepareStatements();

            log.debug("Declared cassandraService in " + Thread.currentThread().getName());

            Random random = new Random();
            long userId = random.nextInt(numUsers) + 1;
            long matchId = random.nextInt(numMatches) + 1;
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime threeDaysLater = currentTime.plusDays(3);
            LocalDateTime reservationStart = generateRandomTimestamp(currentTime, threeDaysLater);
            int reservationDuration = random.nextInt(4) + 1;

            cassandraService.requestSeatReservation(matchId, userId, reservationStart, reservationDuration);
            cassandraService.processReservationRequests();

            log.info("Thread " + Thread.currentThread().getName() + " executed completed!");

        } catch (Exception e) {
            log.error("Error occurred");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
