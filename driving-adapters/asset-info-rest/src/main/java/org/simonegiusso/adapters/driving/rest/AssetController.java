package org.simonegiusso.adapters.driving.rest;


import static org.simonegiusso.adapters.driving.rest.ResponseEntityUtil.toResponse;

import lombok.RequiredArgsConstructor;
import org.simonegiusso.app.domain.Price;
import org.simonegiusso.app.ports.driving.ForGettingMaxPrice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
class AssetController {

    private final ForGettingMaxPrice maxPriceProvider;

    @GetMapping("/{isin}/max-price")
    ResponseEntity<Price> getMaxPrice(@PathVariable String isin) {
        var maxPrice = maxPriceProvider.getMaxPrice(isin);
        return toResponse(maxPrice);
    }

}
