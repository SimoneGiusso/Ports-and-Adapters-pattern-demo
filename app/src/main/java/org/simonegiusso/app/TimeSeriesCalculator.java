package org.simonegiusso.app;

import static java.util.Comparator.comparingDouble;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.simonegiusso.app.domain.Price;
import org.simonegiusso.app.ports.driven.ForGettingTimeseries;
import org.simonegiusso.app.ports.driving.ForGettingMaxPrice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeSeriesCalculator implements ForGettingMaxPrice {

    private final ForGettingTimeseries assetInfoProvider;

    @Override
    public Optional<Price> getMaxPrice(String isin) {
        return assetInfoProvider.getSortedDescTimeSeries(isin).entrySet().stream()
            .max(comparingDouble(Map.Entry::getValue))
            .map(e -> new Price(e.getKey(), e.getValue()));
    }
}
