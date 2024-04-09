package pl.dkaluza.userservice.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
record ErrorResponse(String message, ZonedDateTime timestamp, List<Field> fields) {

    record Field(String name, String message) {}
}
