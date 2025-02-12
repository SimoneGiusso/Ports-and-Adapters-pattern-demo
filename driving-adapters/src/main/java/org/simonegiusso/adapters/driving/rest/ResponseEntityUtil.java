package org.simonegiusso.adapters.driving.rest;

import static org.springframework.http.ResponseEntity.notFound;

import java.util.Optional;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
enum ResponseEntityUtil {
    ;

    static <T> ResponseEntity<T> toResponse(Optional<T> value) {
        return value.map(ResponseEntity::ok)
            .orElseGet(() -> notFound().build());
    }

}
