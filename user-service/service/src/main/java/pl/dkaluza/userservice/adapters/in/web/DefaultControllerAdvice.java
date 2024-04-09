package pl.dkaluza.userservice.adapters.in.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class DefaultControllerAdvice extends ResponseEntityExceptionHandler {
//     TODO customize it later (at best by adjusting all errors to RFC 7807 Problem Details)
}
