package pl.dkaluza.userservice.adapters.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
class UserController {
    @PostMapping("/sign-up")
    ResponseEntity<?> signUp() {
        throw new UnsupportedOperationException();
    }
}
