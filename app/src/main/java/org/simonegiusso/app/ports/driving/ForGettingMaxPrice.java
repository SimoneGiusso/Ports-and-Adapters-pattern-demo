package org.simonegiusso.app.ports.driving;

import java.util.Optional;
import org.simonegiusso.app.domain.Price;

@FunctionalInterface
public interface ForGettingMaxPrice {

    Optional<Price> getMaxPrice(String isin);

}
