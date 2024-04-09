package pl.dkaluza.userservice.adapters.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;

@RestController
@RequestMapping("/user")
class UserController {
    private final UserWebFacade userFacade;

    public UserController(UserWebFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("/sign-up")
    ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) throws ObjectAlreadyPersistedException {
        return userFacade.signUp(signUpRequest);
    }
}
