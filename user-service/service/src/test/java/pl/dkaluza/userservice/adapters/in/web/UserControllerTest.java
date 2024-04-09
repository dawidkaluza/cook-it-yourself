package pl.dkaluza.userservice.adapters.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import pl.dkaluza.userservice.domain.User;
import pl.dkaluza.userservice.domain.exceptions.EmailAlreadyExistsException;
import pl.dkaluza.userservice.ports.in.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    void beforeEach() {
        userService = mock();
        var mapper = new UserWebMapper(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        var facade = new UserWebFacade(mapper, userService);
        userController = new UserController(facade);
    }

    @ParameterizedTest
    @CsvSource({
        "dawid, 123xyz, Dawid, 'email'",
        "dawid@d.c, 1234, Dawid, 'password'",
        "dawid, 123456, D, 'email name'",
    })
    void signUp_invalidData_returnErrorResponse(String email, String password, String name, String expectedErrorFieldsJoint) {
        // Given
        var expectedErrorFields = expectedErrorFieldsJoint.isEmpty() ? new String[] {} : expectedErrorFieldsJoint.split(" ");
        var reqBody = new SignUpRequest(email, password.toCharArray(), name);

        // When
        var response = userController.signUp(reqBody);

        // Then
        assertThat(response)
            .isNotNull();

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(response.getBody())
            .isNotNull()
            .isInstanceOf(ErrorResponse.class);

        var resBody = (ErrorResponse) response.getBody();
        assertThat(resBody.message())
            .isNotNull();

        assertThat(resBody.timestamp())
            .isNotNull();

        assertThat(resBody.fields())
            .extracting(ErrorResponse.Field::name)
            .containsExactly(expectedErrorFields);
    }

    @Test
    void signUp_existingEmail_returnErrorResponse() {
        // Given
        var reqBody = new SignUpRequest("dawid@d.c", "123xyz".toCharArray(), "Dawid");
        when(userService.signUp(any())).thenThrow(new EmailAlreadyExistsException("Given email already exists"));

        // When
        var response = userController.signUp(reqBody);

        // Then
        assertThat(response)
            .isNotNull();

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.CONFLICT);

        assertThat(response.getBody())
            .isNotNull()
            .isInstanceOf(ErrorResponse.class);

        var resBody = (ErrorResponse) response.getBody();
        assertThat(resBody.message())
            .isNotNull();

        assertThat(resBody.timestamp())
            .isNotNull();

        assertThat(resBody.fields())
            .isEmpty();
    }

    @Test
    void signUp_unexpectedException_throwExceptionFurther() {
        // Given
        var reqBody = new SignUpRequest("dawid@d.c", "123xyz".toCharArray(), "Dawid");
        when(userService.signUp(any())).thenThrow(new RuntimeException("Something went wrong"));

        // When, then
        assertThatThrownBy(() -> userController.signUp(reqBody))
            .isExactlyInstanceOf(RuntimeException.class)
            .hasMessage("Something went wrong");
    }

    @Test
    void signUp_validRequest_returnCreatedUser() {
        // Given
        var reqBody = new SignUpRequest("dawid@d.c", "123xyz".toCharArray(), "Dawid");
        when(userService.signUp(any())).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            return User.builder()
                .id(1L)
                .email(user.getEmail().getValue())
                .encodedPassword(user.getPassword().getValue())
                .name(user.getName().getValue())
                .fromPersistenceFactory()
                .produce();
        });

        // When
        var response = userController.signUp(reqBody);

        // Then
        assertThat(response)
            .isNotNull();

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody())
            .isNotNull()
            .isInstanceOf(SignUpResponse.class);

        var resBody = (SignUpResponse) response.getBody();
        assertThat(resBody)
            .isNotNull()
            .extracting(SignUpResponse::id, SignUpResponse::email, SignUpResponse::name)
            .containsExactly(1L, reqBody.email(), reqBody.name());
    }
}