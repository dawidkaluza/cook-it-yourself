package pl.dkaluza.userservice.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.userservice.domain.exceptions.EmailAlreadyExistsException;
import pl.dkaluza.userservice.ports.in.UserService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
class UserWebFacade {
    private final UserWebMapper userMapper;
    private final UserService userService;

    public UserWebFacade(UserWebMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    ResponseEntity<?> signUp(SignUpRequest signUpReq) throws ObjectAlreadyPersistedException {
        try {
            var newUser = userMapper.toUser(signUpReq);
            var createdUser = userService.signUp(newUser);
            var signUpRes = userMapper.toSignUpResponse(createdUser);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(signUpRes);
        } catch (ValidationException e) {
            var errors = e.getErrors();
            var isEncoderInvalid = errors.stream().anyMatch(error -> error.name().equals("passwordEncoder"));
            if (isEncoderInvalid) {
                throw new IllegalStateException("Configured password encoder is not valid", e);
            }

            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                    new ErrorResponse(
                        "Invalid fields values",
                        ZonedDateTime.now(ZoneOffset.UTC),
                        errors.stream()
                            .map(error -> new ErrorResponse.Field(error.name(), error.message()))
                            .collect(Collectors.toList())
                    )
                );
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                    new ErrorResponse(
                        "E-mail already exists",
                        ZonedDateTime.now(ZoneOffset.UTC),
                        Collections.emptyList()
                    )
                );
        }
    }
}
