package pl.dkaluza.userservice.adapters.out.persistence;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.userservice.domain.EmailAddress;
import pl.dkaluza.userservice.domain.User;
import pl.dkaluza.userservice.domain.UserId;
import pl.dkaluza.userservice.ports.out.UserRepository;

import java.util.HashSet;
import java.util.Set;

import static pl.dkaluza.domaincore.Assertions.*;

public class InMemoryUserRepository implements UserRepository {
    private final Set<User> users;
    private long idSeq;

    public InMemoryUserRepository() {
        users = new HashSet<>();
        idSeq = 0L;
    }

    @Override
    public User insertUser(User user) throws ObjectAlreadyPersistedException {
        assertArgument(user != null, "User is null");

        if (user.isPersisted()) {
            throw new ObjectAlreadyPersistedException(user);
        }

        User insertedUser;
        try {
            insertedUser = User.builder()
                .id(++idSeq)
                .email(user.getEmail().getValue())
                .encodedPassword(user.getPassword().getValue())
                .name(user.getName().getValue())
                .fromPersistenceFactory()
                .produce();
        } catch (ValidationException e) {
            throw new IllegalStateException("Could not create a user 'from persistence' basing on provided user", e);
        }

        users.add(insertedUser);
        return insertedUser;
    }

    @Override
    public boolean userExistsByEmail(EmailAddress email) {
        return users.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    public boolean userExistsById(UserId id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }
}
