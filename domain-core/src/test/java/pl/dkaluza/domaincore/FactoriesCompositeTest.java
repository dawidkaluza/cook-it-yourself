package pl.dkaluza.domaincore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class FactoriesCompositeTest {
    @ParameterizedTest
    @CsvSource({
        "' ', ' ', 'sender message'",
        "'Dawid', ' ', 'message'",
        "'', 'Message', 'sender'",
        "'Dawid', 'Message', ''",
    })
    void validate_variousComponents_returnExpectedErrors(String sender, String message, String expectedFieldsErrorsJoint) {
        // Given
        var factory = chatEntryFactory(sender, message);
        var expectedFieldsErrors = expectedFieldsErrorsJoint.isBlank()
            ? new String[] { }
            : expectedFieldsErrorsJoint.split(" ");

        // When
        var errors = factory.validate();

        // Then
        assertThat(errors)
            .extracting(FieldError::getName)
            .containsExactly(expectedFieldsErrors);
    }

    @Test
    void produce_invalidComponents_throwException() {
        // Given
        var factory = chatEntryFactory("", "   ");

        // When
        ValidationException e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e)
            .isNotNull();

        assertThat(e.getErrors())
            .extracting(FieldError::getName)
            .containsExactly("sender", "message");
    }

    @Test
    void produce_validComponents_returnProducedObject() {
        // Given
        var factory = chatEntryFactory("Dawid", "Hello World");

        // When
        String chatEntry = factory.produce();

        // Then
        assertThat(chatEntry)
            .isEqualTo("*** [Dawid]: >Hello World< ***");
    }

    private static FactoriesComposite<String> chatEntryFactory(String sender, String message) {
        var senderFactory = new DefaultFactory<>(
            ValidationExecutor.builder()
                .withValidation(
                    sender != null && !sender.isBlank(),
                    "sender", "Sender must not be blank"
                )
                .build(),
            () -> "[" + sender + "]"
        );

        var messageFactory = new DefaultFactory<>(
            ValidationExecutor.builder()
                .withValidation(
                    message != null && !message.isBlank(),
                    "message", "Message must not be blank"
                )
                .build(),
            () -> ">" + message + "<"
        );

        return new FactoriesComposite<>(
            () -> "*** " + senderFactory.assemble() + ": " + messageFactory.assemble() + " ***",
            senderFactory, messageFactory
        );
    }
}
