package pl.dkaluza.userservice.ports.in;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.userservice.domain.EmailAddress;
import pl.dkaluza.userservice.domain.User;
import pl.dkaluza.userservice.domain.exceptions.EmailAlreadyExistsException;
import pl.dkaluza.userservice.domain.exceptions.UserNotFoundException;

public interface UserService {
    User signUp(User user) throws ObjectAlreadyPersistedException, EmailAlreadyExistsException;

    User loadUserByEmail(EmailAddress email) throws UserNotFoundException;
}
