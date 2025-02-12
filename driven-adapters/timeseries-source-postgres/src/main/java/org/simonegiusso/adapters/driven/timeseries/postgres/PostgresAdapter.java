package org.simonegiusso.adapters.driven.timeseries.postgres;

import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCES.POSTGRES;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCE_PROPERTY;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.simonegiusso.app.ports.driven.ForGettingTimeseries;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = TIMESERIES_SOURCE_PROPERTY, havingValue = POSTGRES)
class PostgresAdapter implements ForGettingTimeseries {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public TreeMap<LocalDate, Double> getSortedDescTimeSeries(String isin) {
        return jdbcTemplate.queryForStream(
            "SELECT date, price FROM timeseries WHERE isin = ? ORDER BY date DESC",
            (rs, rowNum) -> Map.entry(rs.getDate("date").toLocalDate(), rs.getDouble("price")),
            isin
        ).collect(
            TreeMap::new,
            (map, entry) -> map.put(entry.getKey(), entry.getValue()),
            TreeMap::putAll
        );
    }

}
