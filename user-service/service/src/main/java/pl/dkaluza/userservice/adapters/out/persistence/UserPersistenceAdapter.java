package pl.dkaluza.userservice.adapters.out.persistence;

import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.userservice.domain.EmailAddress;
import pl.dkaluza.userservice.domain.User;
import pl.dkaluza.userservice.ports.out.UserRepository;

import static pl.dkaluza.domaincore.Assertions.*;

@Component
class UserPersistenceAdapter implements UserRepository {
    private final UserEntityMapper userMapper;
    private final UserEntityRepository userRepository;

    public UserPersistenceAdapter(UserEntityMapper userMapper, UserEntityRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public User insertUser(User user) throws ObjectAlreadyPersistedException {
        assertArgument(user != null, "User is null");
        ObjectAlreadyPersistedException.throwIfPersisted(user);

        var newUser = userRepository.save(userMapper.toEntity(user));
        return userMapper.toDomain(newUser);
    }

    @Override
    public boolean userExistsByEmail(EmailAddress email) {
        assertArgument(email != null, "Email is null");
        return userRepository.existsByEmail(email.getValue());
    }
}
