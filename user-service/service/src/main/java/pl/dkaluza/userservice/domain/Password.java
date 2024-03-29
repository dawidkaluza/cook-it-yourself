package pl.dkaluza.userservice.domain;

import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.ValidationExecutor;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Password {
    private final char[] encodedPassword;

    private Password(char[] encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public static Factory<Password> of(char[] encodedPassword) {
        return PasswordFactory.of(encodedPassword);
    }

    public static Factory<Password> of(char[] password, Function<char[], char[]> passwordEncoder) {
        return PasswordFactory.of(password, passwordEncoder);
    }

    public char[] getValue() {
        return encodedPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var password = (Password) o;
        return Arrays.equals(encodedPassword, password.encodedPassword);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(encodedPassword);
    }

    static class PasswordFactory extends DefaultFactory<Password> {
        private static final Pattern PASSWORD_PATTERN = Pattern.compile("^\\S+$");

        private PasswordFactory(ValidationExecutor validationExecutor, Supplier<Password> objectSupplier) {
            super(validationExecutor, objectSupplier);
        }

        private static boolean isPasswordValid(char[] password) {
            if (password == null) {
                return false;
            }

            if (password.length < 5 || password.length > 32) {
                return false;
            }

            return PASSWORD_PATTERN.matcher(new String(password)).matches();
        }

        static PasswordFactory of(char[] password, Function<char[], char[]> passwordEncoder) {
            return new PasswordFactory(
                ValidationExecutor.builder()
                    .withValidation(isPasswordValid(password), "password", "Password must have from 5 to 32 non-white chars")
                    .withValidation(passwordEncoder != null, "passwordEncoder", "Password encoder must not be null")
                    .build(),
                () -> {
                    //noinspection DataFlowIssue
                    char[] encodedPassword = passwordEncoder.apply(password);
                    Assertions.assertState(encodedPassword != null, "Password encoder generated null value");
                    return new Password(encodedPassword);
                }
            );
        }

        static PasswordFactory of(char[] encodedPassword) {
            return new PasswordFactory(
                ValidationExecutor.builder()
                    .withValidation(encodedPassword != null, "encodedPassword", "Encoded password must not be null")
                    .build(),
                () -> new Password(encodedPassword)
            );
        }

        @Override
        protected Supplier<Password> getObjectSupplier() {
            return super.getObjectSupplier();
        }
    }
}
