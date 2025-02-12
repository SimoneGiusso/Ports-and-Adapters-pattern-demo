package org.simonegiusso.configurator;

import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCES.CASSANDRA;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCE_PROPERTY;
import static org.simonegiusso.utils.test.constants.Constants.CASSANDRA_DOCKER_IMAGE;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers // Cassandra driver does a connection check when context is loaded
@TestPropertySource(properties = TIMESERIES_SOURCE_PROPERTY + "=" + CASSANDRA)
class WithTimeSeriesSourceCassandraStartUpITest extends BaseSpringStartUpITest {

    @Container
    @ServiceConnection
    private static CassandraContainer cassandraContainer = new CassandraContainer(DockerImageName.parse(CASSANDRA_DOCKER_IMAGE))
        .withInitScript("cassandra-init.sql");

}