package pl.dkaluza.userservice.adapters.in.web.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.userservice.domain.EmailAddress;
import pl.dkaluza.userservice.domain.exceptions.UserNotFoundException;
import pl.dkaluza.userservice.ports.in.UserService;

class DefaultUserDetailsService implements UserDetailsService {
    private final UserDetailsMapper userDetailsMapper;
    private final UserService userService;

    public DefaultUserDetailsService(UserDetailsMapper userDetailsMapper, UserService userService) {
        this.userDetailsMapper = userDetailsMapper;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            var user = userService.loadUserByEmail(
                EmailAddress.of(email).produce()
            );
            return userDetailsMapper.toUserDetails(user);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("User with email=" + email + " not found", e);
        } catch (ValidationException e) {
            throw new UsernameNotFoundException("Email=" + email + " is not valid", e);
        }
    }
}
