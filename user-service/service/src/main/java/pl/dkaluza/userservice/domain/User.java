package pl.dkaluza.userservice.domain;

import pl.dkaluza.domaincore.AbstractPersistable;
import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.FactoriesComposite;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.userservice.domain.EmailAddress.EmailAddressFactory;
import pl.dkaluza.userservice.domain.Password.PasswordFactory;
import pl.dkaluza.userservice.domain.UserId.UserIdFactory;
import pl.dkaluza.userservice.domain.UserName.UserNameFactory;

import java.util.function.Function;

public class User extends AbstractPersistable<UserId> {
    private final EmailAddress email;
    private final Password password;
    private final UserName name;

    private User(UserId id, EmailAddress email, Password password, UserName name) {
        super(id);
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public UserName getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String email;
        private char[] password;
        private Function<char[], char[]> passwordEncoder;
        private char[] encodedPassword;
        private String name;

        private Builder() { }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(char[] password) {
            this.password = password;
            return this;
        }

        public Builder passwordEncoder(Function<char[], char[]> passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public Builder encodedPassword(char[] encodedPassword) {
            this.encodedPassword = encodedPassword;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Factory<User> newUserFactory() {
            var emailFactory = new EmailAddressFactory(email);
            var passwordFactory = PasswordFactory.of(password, passwordEncoder);
            var nameFactory = new UserNameFactory(name);
            return new FactoriesComposite<>(
                () -> new User(
                    null, emailFactory.getObjectSupplier().get(),
                    passwordFactory.getObjectSupplier().get(), nameFactory.getObjectSupplier().get()
                ),
                emailFactory, passwordFactory, nameFactory
            );
        }

        public Factory<User> fromPersistenceFactory() {
            var idFactory = new UserIdFactory(id);
            var emailFactory = new EmailAddressFactory(email);
            var passwordFactory = PasswordFactory.of(encodedPassword);
            var nameFactory = new UserNameFactory(name);
            return new FactoriesComposite<>(
                () -> new User(
                    idFactory.getObjectSupplier().get(), emailFactory.getObjectSupplier().get(),
                    passwordFactory.getObjectSupplier().get(), nameFactory.getObjectSupplier().get()
                ),
                idFactory, emailFactory, passwordFactory, nameFactory
            );
        }
    }


}
