package org.simonegiusso.adapters.driven.timeseries.cassandra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.simonegiusso.utils.test.constants.Constants.APPLE_ISIN;

import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.simonegiusso.utils.test.constants.Constants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ContextConfiguration(classes = {SpringBootConfig.class, CassandraAdapter.class})
@DataCassandraTest
@EnableAutoConfiguration
class CassandraAdapterITest {

    private static final Map<LocalDate, Double> EXPECTED_TIMESERIES = new TreeMap<>(Map.of(
        LocalDate.of(2023, 1, 1), 150.0,
        LocalDate.of(2023, 2, 1), 155.0,
        LocalDate.of(2023, 3, 1), 160.0,
        LocalDate.of(2023, 4, 1), 165.0,
        LocalDate.of(2023, 5, 1), 170.0,
        LocalDate.of(2023, 6, 1), 175.0,
        LocalDate.of(2023, 7, 1), 154.0
    ));

    @Container
    @ServiceConnection
    private static CassandraContainer cassandraContainer =
        new CassandraContainer(DockerImageName.parse(Constants.CASSANDRA_DOCKER_IMAGE))
            .withInitScript("init.sql");

    @Inject
    private CassandraAdapter cassandraAdapter;

    @Test
    void get_timeseries_from_db() {
        var timeSeries = cassandraAdapter.getSortedDescTimeSeries(APPLE_ISIN);

        assertEquals(EXPECTED_TIMESERIES, timeSeries);
    }

}