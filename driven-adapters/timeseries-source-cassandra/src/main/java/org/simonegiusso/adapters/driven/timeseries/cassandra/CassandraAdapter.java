package org.simonegiusso.adapters.driven.timeseries.cassandra;

import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCES.CASSANDRA;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCE_PROPERTY;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.simonegiusso.app.ports.driven.ForGettingTimeseries;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = TIMESERIES_SOURCE_PROPERTY, havingValue = CASSANDRA)
public class CassandraAdapter implements ForGettingTimeseries {

    private final CqlTemplate cqlTemplate;

    @Override
    public TreeMap<LocalDate, Double> getSortedDescTimeSeries(String isin) {
        return cqlTemplate.queryForStream(
            "SELECT date, price FROM timeseries WHERE isin = ? ORDER BY date DESC",
            (rs, rowNum) -> Map.entry(rs.getLocalDate("date"), rs.getDouble("price")),
            isin
        ).collect(
            TreeMap::new,
            (map, entry) -> map.put(entry.getKey(), entry.getValue()),
            TreeMap::putAll
        );
    }

}
