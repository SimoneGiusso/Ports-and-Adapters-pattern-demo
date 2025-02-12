package org.simonegiusso.adapters.driven.timeseries.cassandra;

import static org.simonegiusso.utils.Constants.DRIVEN_TIMESERIES_PROPERTY;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCES.CASSANDRA;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCE_PROPERTY;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.core.cql.CqlTemplate;

@SuppressWarnings("MethodMayBeStatic")
@ConditionalOnProperty(value = TIMESERIES_SOURCE_PROPERTY, havingValue = CASSANDRA)
@Configuration
class SpringBootConfig {

    @Bean
    @Primary
    @ConfigurationProperties(DRIVEN_TIMESERIES_PROPERTY + "." + CASSANDRA)
    CassandraProperties cassandraProperties() {
        return new CassandraProperties();
    }

    @Bean
    public CqlTemplate cqlTemplate(CqlSession session, CassandraProperties properties) {
        return new CqlTemplate(session);
    }

}