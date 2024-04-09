package pl.dkaluza.userservice.adapters.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.userservice.domain.EmailAddress;
import pl.dkaluza.userservice.domain.User;
import pl.dkaluza.userservice.domain.UserName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPersistenceAdapterTest {
    private InMemoryUserPersistenceAdapter userPersistenceAdapter;

    @BeforeEach
    void beforeEach() {
        userPersistenceAdapter = new InMemoryUserPersistenceAdapter();
    }

    @ParameterizedTest
    @NullSource
    void insertUser_invalidParams_throwException(User user) {
        assertThatThrownBy(() -> userPersistenceAdapter.insertUser(user))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertUser_alreadyExistingUser_throwException() {
        // Given
        var newUser = User.builder()
            .email("dawid@c.d")
            .password("123xyz".toCharArray())
            .passwordEncoder((pwd) -> pwd)
            .name("Dawid")
            .newUserFactory()
            .produce();

        var existingUser = userPersistenceAdapter.insertUser(newUser);

        // When
        // Then
        assertThatThrownBy(() -> userPersistenceAdapter.insertUser(existingUser))
            .isInstanceOf(ObjectAlreadyPersistedException.class);

    }

    @Test
    void insertUser_newUser_returnCreatedUser() {
        // Given
        var newUser = User.builder()
            .email("dawid@c.d")
            .password("123xyz".toCharArray())
            .passwordEncoder((pwd) -> pwd)
            .name("Dawid")
            .newUserFactory()
            .produce();

        // When
        var savedUser = userPersistenceAdapter.insertUser(newUser);

        // Then
        assertThat(savedUser)
            .isNotNull();

        assertThat(savedUser.getId())
            .isNotNull();

        assertThat(savedUser.getEmail())
            .isEqualTo(newUser.getEmail());

        assertThat(savedUser.getPassword())
            .isNotNull();

        assertThat(savedUser.getName())
            .isEqualTo(newUser.getName());
    }

    @ParameterizedTest
    @NullSource
    void findUserByEmail_invalidParams_throwException(EmailAddress email) {
        assertThatThrownBy(() -> userPersistenceAdapter.findUserByEmail(email))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "dawid@d.c, true",
        "gabriel@d.c, false"
    })
    void findUserByEmail_validEmail_returnOptionalUser(String email, boolean exists) {
        // Given
        var user = User.builder()
            .email("dawid@d.c")
            .password("123xyz".toCharArray())
            .passwordEncoder((pwd) -> pwd)
            .name("Dawid")
            .newUserFactory()
            .produce();

        userPersistenceAdapter.insertUser(user);

        // When
        var optionallyFoundUser = userPersistenceAdapter.findUserByEmail(
            EmailAddress.of(email).produce()
        );

        // Then
        if (exists) {
            assertThat(optionallyFoundUser)
                .isPresent()
                .get()
                .extracting(User::getEmail, User::getName)
                .containsExactly(EmailAddress.of("dawid@d.c").produce(), UserName.of("Dawid").produce());
        } else {
            assertThat(optionallyFoundUser)
                .isEmpty();
        }
    }

    @ParameterizedTest
    @NullSource
    void userExistsByEmail_invalidParams_throwException(EmailAddress email) {
        assertThatThrownBy(() -> userPersistenceAdapter.userExistsByEmail(email))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "dawid@d.c, true",
        "gabriel@d.c, false"
    })
    void userExistsByEmail_validEmail_returnExpectedResponse(String givenEmail, boolean expectedResponse) {
        // Given
        var user = User.builder()
            .email("dawid@d.c")
            .password("123xyz".toCharArray())
            .passwordEncoder((pwd) -> pwd)
            .name("Dawid")
            .newUserFactory()
            .produce();

        userPersistenceAdapter.insertUser(user);

        // When
        boolean actualResponse = userPersistenceAdapter.userExistsByEmail(
            EmailAddress.of(givenEmail).produce()
        );

        // Then
        assertThat(actualResponse)
            .isEqualTo(expectedResponse);
    }
}