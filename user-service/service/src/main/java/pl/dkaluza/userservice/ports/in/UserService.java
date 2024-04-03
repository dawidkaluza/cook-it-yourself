package pl.dkaluza.userservice.ports.in;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.userservice.domain.User;
import pl.dkaluza.userservice.domain.exceptions.EmailAlreadyExistsException;

public interface UserService {
    User signUp(User user) throws ObjectAlreadyPersistedException, EmailAlreadyExistsException;
}
