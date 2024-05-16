package pl.dkaluza.domaincore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class PageTest {
    @ParameterizedTest
    @MethodSource("ofInvalidParamsProvider")
    void of_invalidParams_throwException(List<String> items, int pageNumber, int totalPages, String[] expectedFieldErrors) {
        // Given
        var factory = Page.of(items, pageNumber, totalPages);

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

    private static Stream<Arguments> ofInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                null, 0, 0,
                new String[] { "items", "pageNumber", "totalPages" }
            ),
            Arguments.of(
                List.of(), -1, -1,
                new String[] { "pageNumber", "totalPages" }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("ofValidParamsProvider")
    void of_validParams_returnNewPage(List<String> items, int pageNumber, int totalPages) {
        // Given
        var factory = Page.of(items, pageNumber, totalPages);

        // When
        var page = factory.produce();

        // Then
        assertThat(page)
            .isNotNull();

        assertThat(page.getItems())
            .isEqualTo(items);

        assertThat(page.getPageNumber())
            .isEqualTo(pageNumber);

        assertThat(page.getTotalPages())
            .isEqualTo(totalPages);
    }

    private static Stream<Arguments> ofValidParamsProvider() {
        return Stream.of(
            Arguments.of(
                List.of(), 1, 1
            ),

            Arguments.of(
                List.of("a", "b", "c"), 123213, 213213213
            )
        );
    }

}