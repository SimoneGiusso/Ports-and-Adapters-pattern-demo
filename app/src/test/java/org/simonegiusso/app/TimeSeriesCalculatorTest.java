package org.simonegiusso.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.simonegiusso.app.domain.Price;
import org.simonegiusso.app.ports.driven.ForGettingAssetInfo;

@ExtendWith(MockitoExtension.class)
class TimeSeriesCalculatorTest {

    private static final Price MAX_PRICE = new Price(LocalDate.of(2021, 1, 3), 172.0);
    private static final TreeMap<LocalDate, Double> TIMESERIES = new TreeMap<>(Map.of(
        LocalDate.of(2021, 1, 1), 150.0,
        LocalDate.of(2021, 1, 2), 123.0,
        MAX_PRICE.date(), MAX_PRICE.value(),
        LocalDate.of(2021, 1, 4), 145.0,
        LocalDate.of(2021, 1, 5), 156.0
    ));
    private static final String APPLE_ISIN = "US0378331005";

    @Mock
    private ForGettingAssetInfo assetInfoProvider;

    @InjectMocks
    private TimeSeriesCalculator timeSeriesCalculator;

    @Test
    void compute_the_max_price_from_a_timeseries() {
        when(assetInfoProvider.getSortedDescTimeSeries(APPLE_ISIN)).thenReturn(TIMESERIES);

        var maxPrice = timeSeriesCalculator.getMaxPrice(APPLE_ISIN).orElseThrow();

        assertEquals(MAX_PRICE, maxPrice);
    }
}