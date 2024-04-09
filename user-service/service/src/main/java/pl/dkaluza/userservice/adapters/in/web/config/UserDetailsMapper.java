package pl.dkaluza.userservice.adapters.in.web.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.dkaluza.userservice.domain.User;

import java.util.Collections;

@Component
class UserDetailsMapper {
    UserDetails toUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
            user.getEmail().getValue(),
            new String(user.getPassword().getValue()),
            Collections.emptyList()
        );
    }
}
