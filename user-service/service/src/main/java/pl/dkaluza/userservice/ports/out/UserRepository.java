package pl.dkaluza.userservice.ports.out;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.userservice.domain.EmailAddress;
import pl.dkaluza.userservice.domain.User;

import java.util.Optional;

public interface UserRepository {
    User insertUser(User user) throws ObjectAlreadyPersistedException;

    Optional<User> findUserByEmail(EmailAddress email);

    boolean userExistsByEmail(EmailAddress email);
}
