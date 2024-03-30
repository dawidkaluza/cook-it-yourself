package pl.dkaluza.domaincore;

import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.Collections;
import java.util.List;

/**
 * Factory is an object that takes responsibility for both validation and creation of domain objects.
 * Instance of this object is supposed to create one instance of target domain object.
 * <p></p>
 * Factory can go through various processes.
 * <p></p>
 * Internal processes - accessible only within factories.
 * <ul>
 *     <li>validate - check components of which given domain object is to be created..Return errors if any occurred.</li>
 *     <li>assemble - create domain object and return it, even when validation returned errors!
 *     Using this method it's your responsibility to make sure, that created object is valid.</li>
 * </ul>
 * Public processes - accessible internally and externally (by factories' users).
 * <ul>
 *     <li>review - perform validation and mark object as reviewed. Return errors if any occurred.</li>
 *     <li>produce - perform review (if not done yet) and throw exception if review returned any errors; otherwise assemble and return domain object.</li>
 * </ul>
 * @param <T> type of domain object
 */
public abstract class Factory<T> {
    private final Assembler<T> assembler;

    private List<FieldError> errors;
    private boolean isReviewed;

    public Factory(Assembler<T> assembler) {
        this.assembler = assembler;

        errors = Collections.emptyList();
        isReviewed = false;
    }

    protected abstract List<FieldError> validate();

    protected T assemble() {
        return assembler.assemble();
    }

    public List<FieldError> review() {
        if (isReviewed) {
            return errors;
        }

        this.errors = validate();
        isReviewed = true;
        return Collections.unmodifiableList(errors);
    }

    public T produce() throws ValidationException {
        if (!isReviewed) {
            review();
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return assemble();
    }
}
