package org.simonegiusso.adapters.driving.rest;

import static java.time.LocalDate.now;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;
import static org.simonegiusso.utils.test.constants.Constants.APPLE_ISIN;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.simonegiusso.app.domain.Price;
import org.simonegiusso.app.ports.driving.ForGettingMaxPrice;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_CLASS)
class AssetControllerITests {

    private static final Price MAX_PRICE = new Price(now(), 176.0);
    private static final Optional<Price> OPTIONAL_MAX_PRICE = Optional.of(MAX_PRICE);
    private static final Optional<Price> NO_PRICE = empty();
    private static final String MAX_PRICE_URL = "/assets/" + APPLE_ISIN + "/max-price";

    @MockitoBean
    private ForGettingMaxPrice forGettingMaxPrice;

    @Inject
    private AssetController assetController;

    @Inject
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private String port;

    @Inject
    private Environment environment;

    @BeforeAll
    void restTemplateSetUp() {
        restTemplate.setUriTemplateHandler(new LocalHostUriTemplateHandler(environment));
    }

    @Test
    void convert_the_max_price_to_http_response() {
        when(forGettingMaxPrice.getMaxPrice(APPLE_ISIN)).thenReturn(OPTIONAL_MAX_PRICE);

        ResponseEntity<Price> response = restTemplate.getForEntity(MAX_PRICE_URL, Price.class);

        assertEquals(OK, response.getStatusCode());
        assertEquals(MAX_PRICE, response.getBody());
    }

    @Test
    final void return_not_found_if_asset_does_not_exist() {
        when(forGettingMaxPrice.getMaxPrice(APPLE_ISIN)).thenReturn(NO_PRICE);

        ResponseEntity<Double> response = restTemplate.getForEntity(MAX_PRICE_URL, Double.class);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }

}