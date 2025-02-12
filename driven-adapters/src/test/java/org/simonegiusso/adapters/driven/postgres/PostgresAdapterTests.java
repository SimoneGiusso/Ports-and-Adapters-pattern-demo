package org.simonegiusso.adapters.driven.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ContextConfiguration(classes = PostgresAdapter.class)
@JdbcTest
class PostgresAdapterTests {

    private static final String APPLE_ISIN = "US0378331005";
    private static final Map<LocalDate, Double> EXPECTED_TIMESERIES = new TreeMap<>(Map.of(
        LocalDate.of(2023, 1, 1), 150.0,
        LocalDate.of(2023, 2, 1), 155.0,
        LocalDate.of(2023, 3, 1), 160.0,
        LocalDate.of(2023, 4, 1), 165.0,
        LocalDate.of(2023, 5, 1), 170.0,
        LocalDate.of(2023, 6, 1), 175.0,
        LocalDate.of(2023, 7, 1), 154.0
    ));

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgresContainer =
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withInitScript("init.sql");

    @Inject
    private PostgresAdapter postgresAdapter;

    @Test
    void get_timeseries_from_db() {
        var timeSeries = postgresAdapter.getSortedDescTimeSeries(APPLE_ISIN);

        assertEquals(EXPECTED_TIMESERIES, timeSeries);
    }

}