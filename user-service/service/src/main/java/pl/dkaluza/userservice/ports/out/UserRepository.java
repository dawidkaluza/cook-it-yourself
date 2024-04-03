package pl.dkaluza.userservice.ports.out;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.userservice.domain.EmailAddress;
import pl.dkaluza.userservice.domain.User;

public interface UserRepository {
    User insertUser(User user) throws ObjectAlreadyPersistedException;

    boolean userExistsByEmail(EmailAddress email);
}
