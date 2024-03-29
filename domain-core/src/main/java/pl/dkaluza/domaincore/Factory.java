package pl.dkaluza.domaincore;

import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.List;

public interface Factory<T> {
    List<FieldError> validate();

    T create() throws ValidationException;
}
