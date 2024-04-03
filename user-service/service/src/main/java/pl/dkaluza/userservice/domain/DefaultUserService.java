package pl.dkaluza.userservice.domain;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.userservice.domain.events.SignUpEvent;
import pl.dkaluza.userservice.domain.exceptions.EmailAlreadyExistsException;
import pl.dkaluza.userservice.ports.in.UserService;
import pl.dkaluza.userservice.ports.out.UserEventPublisher;
import pl.dkaluza.userservice.ports.out.UserRepository;

import static pl.dkaluza.domaincore.Assertions.*;

class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;

    public DefaultUserService(UserRepository userRepository, UserEventPublisher userEventPublisher) {
        this.userRepository = userRepository;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public User signUp(User user) throws ObjectAlreadyPersistedException, EmailAlreadyExistsException {
        assertArgument(user != null, "User is null");

        var email = user.getEmail();
        if (userRepository.userExistsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        var insertedUser = userRepository.insertUser(user);
        userEventPublisher.publish(new SignUpEvent(insertedUser));
        return insertedUser;
    }
}
