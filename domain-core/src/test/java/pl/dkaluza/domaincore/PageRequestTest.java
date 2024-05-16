package pl.dkaluza.domaincore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class PageRequestTest {
    @ParameterizedTest
    @CsvSource({
        "0, 0, 'pageNumber pageSize'",
        "-1, 1, 'pageNumber'"
    })
    void of_invalidParams_throwException(int pageNumber, int pageSize, String expectedFieldErrorsJoint) {
        // Given
        String[] expectedFieldErrors = expectedFieldErrorsJoint.split(" ");
        var factory = PageRequest.of(pageNumber, pageSize);

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e)
            .isNotNull();

        var errors = e.getErrors();
        assertThat(errors)
            .extracting(FieldError::name)
            .contains(expectedFieldErrors);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "3123213, 321333"
    })
    void of_validParams_returnNewRequest(int pageNumber, int pageSize) {
        // Given
        var factory = PageRequest.of(pageNumber, pageSize);

        // When
        var pageReq = factory.produce();

        // Then
        assertThat(pageReq)
            .isNotNull();

        assertThat(pageReq.getPageNumber())
            .isEqualTo(pageNumber);

        assertThat(pageReq.getPageSize())
            .isEqualTo(pageSize);
    }
}
