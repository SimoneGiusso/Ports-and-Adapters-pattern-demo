package org.simonegiusso.app.ports.driven;

import java.time.LocalDate;
import java.util.TreeMap;

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface ForGettingTimeseries {

    TreeMap<LocalDate, Double> getSortedDescTimeSeries(String isin);

}
