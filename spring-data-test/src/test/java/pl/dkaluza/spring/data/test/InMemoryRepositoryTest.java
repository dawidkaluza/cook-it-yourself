package pl.dkaluza.spring.data.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class InMemoryRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository = new UserRepository();
    }

    @Test
    void save_newUser_returnPersistedWithId() {
        // Given
        var newUser = new User(null, "Dawid");

        // When
        var savedUser = userRepository.save(newUser);

        // Then
        assertThat(savedUser)
            .isNotNull()
            .extracting(User::id, User::name)
            .containsExactly(1L, newUser.name());

        assertThat(userRepository.existsById(savedUser.id()))
            .isTrue();
    }

    @Test
    void save_unchangedUser_returnFromPersistence() {
        // Given
        var newUser = new User(null, "Dawid");
        var savedUser = userRepository.save(newUser);

        // When
        savedUser = userRepository.save(savedUser);

        // Then
        assertThat(savedUser)
            .isNotNull()
            .extracting(User::id, User::name)
            .containsExactly(1L, newUser.name());

        assertThat(userRepository.existsById(savedUser.id()))
            .isTrue();
    }

    @Test
    void save_changedUser_returnUpdatedFromPersistence() {
        // Given
        var newUser = new User(null, "Dawid");
        var savedUser = userRepository.save(newUser);
        var changedUser = new User(savedUser.id(), "David");

        // When
        savedUser = userRepository.save(changedUser);

        // Then
        assertThat(savedUser)
            .isNotNull()
            .extracting(User::id, User::name)
            .containsExactly(changedUser.id(), changedUser.name());

        assertThat(userRepository.existsById(savedUser.id()))
            .isTrue();
    }

    @Test
    void save_newUserAlongOtherUsers_returnPersistedWithId() {
        // Given
        userRepository.saveAll(
            List.of(
                new User(null, "Dawid"),
                new User(null, "Gabriel")
            )
        );
        var newUser = new User(null, "Nicolas");

        // When
        var savedUser = userRepository.save(newUser);

        // Then
        assertThat(savedUser)
            .isNotNull()
            .extracting(User::id, User::name)
            .containsExactly(3L, "Nicolas");
    }

    // TODO write the rest of the tests

}