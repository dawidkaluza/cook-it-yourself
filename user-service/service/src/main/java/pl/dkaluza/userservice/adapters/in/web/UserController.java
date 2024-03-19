package pl.dkaluza.userservice.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
class UserController {
    @GetMapping("/hello")
    String helloWorld() {
        return "Hello world";
    }
}
