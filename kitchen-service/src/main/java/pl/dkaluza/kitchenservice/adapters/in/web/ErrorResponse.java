package pl.dkaluza.kitchenservice.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
record ErrorResponse(String message, ZonedDateTime timestamp, List<Field> fields) {
    ErrorResponse(String message, ZonedDateTime timestamp) {
        this(message, timestamp, Collections.emptyList());
    }

    record Field(String name, String message) {}
}