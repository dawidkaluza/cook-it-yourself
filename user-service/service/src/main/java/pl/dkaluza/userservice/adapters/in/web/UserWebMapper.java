package pl.dkaluza.userservice.adapters.in.web;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.userservice.domain.User;

@Component
class UserWebMapper {
    private final PasswordEncoder passwordEncoder;

    public UserWebMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    User toUser(SignUpRequest request) throws ValidationException {
        return User.builder()
            .email(request.email())
            .password(request.password())
            .passwordEncoder((pwd) -> passwordEncoder.encode(new String(request.password())).toCharArray())
            .name(request.name())
            .newUserFactory()
            .produce();
    }

    SignUpResponse toSignUpResponse(User user) {
        return new SignUpResponse(
            user.getId().getId(),
            user.getEmail().getValue(),
            user.getName().getValue()
        );
    }
}
