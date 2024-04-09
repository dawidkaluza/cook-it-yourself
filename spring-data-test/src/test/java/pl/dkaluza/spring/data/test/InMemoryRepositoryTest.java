package pl.dkaluza.spring.data.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

    @Test
    void findAll_pageRequest_returnExpectedUsers() {
        // Given
        userRepository.saveAll(
            List.of(
                new User(null, "Dawid"),
                new User(null, "Gabriel"),
                new User(null, "Nico"),
                new User(null, "Don"),
                new User(null, "David")
            )
        );

        // When
        var users = userRepository.findAll(PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "id")));

        // Then
        assertThat(users)
            .isNotNull();

        assertThat(users.getTotalPages())
            .isEqualTo(3);

        assertThat(users.getTotalElements())
            .isEqualTo(5);

        var content = users.getContent();

        assertThat(content)
            .hasSize(2)
            .extracting(User::name)
            .containsExactly("Nico", "Don");

    }

    // TODO write the rest of the tests

}