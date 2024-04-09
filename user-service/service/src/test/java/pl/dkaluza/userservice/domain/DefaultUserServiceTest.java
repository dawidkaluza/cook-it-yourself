package pl.dkaluza.userservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.userservice.adapters.out.eventpublisher.InMemoryUserEventPublisherAdapter;
import pl.dkaluza.userservice.adapters.out.persistence.InMemoryUserPersistenceAdapter;
import pl.dkaluza.userservice.domain.events.SignUpEvent;
import pl.dkaluza.userservice.domain.exceptions.EmailAlreadyExistsException;
import pl.dkaluza.userservice.domain.exceptions.UserNotFoundException;

import static org.assertj.core.api.Assertions.*;

class DefaultUserServiceTest {
    private InMemoryUserPersistenceAdapter userRepository;
    private InMemoryUserEventPublisherAdapter userEventPublisher;
    private DefaultUserService userService;

    @BeforeEach
    void beforeEach() {
        userRepository = new InMemoryUserPersistenceAdapter();
        userEventPublisher = new InMemoryUserEventPublisherAdapter();
        userService = new DefaultUserService(userRepository, userEventPublisher);
    }

    @ParameterizedTest
    @NullSource
    void signUp_invalidParams_throwException(User user) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> userService.signUp(user));
    }

    @Test
    void signUp_emailAlreadyExists_throwException() {
        // Given
        var existingUser = newUser("dawid@d.c", "12346", "Dawid");
        userRepository.insertUser(existingUser);
        var newUser = newUser("dawid@d.c", "98765", "Dawid");

        // When
        // Then
        assertThatExceptionOfType(EmailAlreadyExistsException.class)
            .isThrownBy(() -> userService.signUp(newUser));
    }

    @Test
    void signUp_userAlreadyPersisted_throwException() {
        // Given
        var user = User.builder()
            .id(1L)
            .email("dawid@d.c")
            .encodedPassword("123as89&*^%^&*%".toCharArray())
            .name("Dawid")
            .fromPersistenceFactory()
            .produce();

        // When
        // Then
        assertThatExceptionOfType(ObjectAlreadyPersistedException.class)
            .isThrownBy(() -> userService.signUp(user));
    }

    @Test
    void signUp_validUser_returnCreatedUser() {
        // Given
        var existingUser = newUser("dawid@gmail.com", "12346", "Dawid");
        userRepository.insertUser(existingUser);

        var email = "dawid@d.c";
        var name = "Dawid";
        var user = newUser(email, "12346", name);

        // When
        user = userService.signUp(user);

        // Then
        assertThat(user)
            .isNotNull();

        assertThat(user.getId())
            .isNotNull();

        assertThat(user.isPersisted())
            .isTrue();

        assertThat(user.getEmail())
            .isNotNull()
            .extracting(EmailAddress::getValue)
            .isEqualTo(email);

        assertThat(user.getPassword())
            .isNotNull();

        assertThat(user.getName())
            .isNotNull()
            .extracting(UserName::getValue)
            .isEqualTo(name);

        var existsInRepo = userRepository.userExistsByEmail(user.getEmail());
        assertThat(existsInRepo)
            .isTrue();

        SignUpEvent event = userEventPublisher.dequeue();
        assertThat(event)
            .isNotNull()
            .extracting(SignUpEvent::id)
            .isEqualTo(user.getId());
    }

    @ParameterizedTest
    @NullSource
    void loadUserByEmail_invalidParams_throwException(EmailAddress email) {
        assertThatThrownBy(() -> userService.loadUserByEmail(email))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loadUserByEmail_nonExistingEmail_throwException() {
        // Given
        var newUser = newUser("dawid@d.c", "123xyz", "Dawid");
        userRepository.insertUser(newUser);

        // When, then
        assertThatThrownBy(() -> userService.loadUserByEmail(
            EmailAddress.of("gabriel@d.c").produce()
        )).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void loadUserByEmail_existingEmail_returnUser() {
        // Given
        var newUser = newUser("dawid@d.c", "123xyz", "Dawid");
        userRepository.insertUser(newUser);

        // When
        var foundUser = userService.loadUserByEmail(
            EmailAddress.of("dawid@d.c").produce()
        );

        // Then
        assertThat(foundUser)
            .isNotNull()
            .extracting(User::getEmail, User::getName)
            .containsExactly(newUser.getEmail(), newUser.getName());
    }

    private User newUser(String email, String password, String name) throws ValidationException {
        return User.builder()
            .email(email)
            .password(password.toCharArray())
            .passwordEncoder((pwd) -> pwd)
            .name(name)
            .newUserFactory()
            .produce();
    }
}