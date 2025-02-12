package org.simonegiusso.adapters.driven.postgres;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.simonegiusso.app.ports.driven.ForGettingAssetInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value="asset-info-source", havingValue = "Postgres")
public class PostgresAdapter implements ForGettingAssetInfo {

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
