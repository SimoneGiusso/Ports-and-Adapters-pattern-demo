package org.simonegiusso.adapters.driven.timeseries.postgres;

import static org.simonegiusso.utils.Constants.DRIVEN_TIMESERIES_PROPERTY;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCES.POSTGRES;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCE_PROPERTY;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("MethodMayBeStatic")
@Configuration
@ConditionalOnProperty(value = TIMESERIES_SOURCE_PROPERTY, havingValue = POSTGRES)
public class SpringBootConfig {

    @Bean
    @ConfigurationProperties(DRIVEN_TIMESERIES_PROPERTY + "." + POSTGRES)
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

}
