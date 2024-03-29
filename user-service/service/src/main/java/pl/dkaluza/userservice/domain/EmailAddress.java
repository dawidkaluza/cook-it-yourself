package pl.dkaluza.userservice.domain;

import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.ValidationExecutor;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public class EmailAddress {
    private final String email;

    private EmailAddress(String email) {
        this.email = email;
    }

    public static Factory<EmailAddress> of(String email) {
        return new EmailAddressFactory(email);
    }

    public String getValue() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var that = (EmailAddress) o;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public String toString() {
        return email;
    }

    static class EmailAddressFactory extends DefaultFactory<EmailAddress> {
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^(\\S+@\\S+\\.\\S+)$");

        EmailAddressFactory(String email) {
            super(
                ValidationExecutor.builder()
                    .withValidation(isEmailValid(email), "email", "E-mail must be valid")
                    .build(),
                () -> new EmailAddress(email)
            );
        }

        @Override
        protected EmailAddress assemble() {
            return super.assemble();
        }

        private static boolean isEmailValid(String email) {
            if (email == null) {
                return false;
            }

            var emailSize = email.length();
            if (emailSize < 3 || emailSize > 128) {
                return false;
            }

            return EMAIL_PATTERN.matcher(email).matches();
        }
    }
}
