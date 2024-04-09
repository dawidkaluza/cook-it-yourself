package pl.dkaluza.userservice.adapters.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

@RestControllerAdvice
class DefaultControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultControllerAdvice.class);

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<?> unexpectedExceptionHandler(RuntimeException exception) {
        logger.error("Unexpected exception caught by controller advice", exception);

        return ResponseEntity.internalServerError().body(
            new ErrorResponse(
                "Error within the server. Try again later.",
                ZonedDateTime.now(ZoneOffset.UTC),
                Collections.emptyList()
            )
        );
    }

    //     TODO customize it later (at best by adjusting all errors to RFC 7807 Problem Details)
}
