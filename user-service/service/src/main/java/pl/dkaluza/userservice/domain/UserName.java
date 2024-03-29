package pl.dkaluza.userservice.domain;

import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.ValidationExecutor;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public class UserName {
    private final String name;

    private UserName(String name) {
        this.name = name;
    }

    public static Factory<UserName> of(String name) {
        return new UserNameFactory(name);
    }

    public String getValue() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var userName = (UserName) o;
        return name.equals(userName.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    static class UserNameFactory extends DefaultFactory<UserName> {
        private static final Pattern NAME_PATTERN = Pattern.compile("^\\S.+\\S$");

        UserNameFactory(String name) {
            super(
                ValidationExecutor.builder()
                    .withValidation(isNameValid(name), "name", "Name must have from 3 to 128 chars")
                    .build(),
                () -> new UserName(name)
            );
        }

        private static boolean isNameValid(String name) {
            if (name == null) {
                return false;
            }

            var nameLength = name.length();
            if (nameLength < 3 || nameLength > 128) {
                return false;
            }

            return NAME_PATTERN.matcher(name).matches();
        }

        @Override
        protected UserName assemble() {
            return super.assemble();
        }
    }
}
