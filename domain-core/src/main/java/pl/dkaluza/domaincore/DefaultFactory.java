package pl.dkaluza.domaincore;

import java.util.List;

/**
 * Factory where validation is dependent on provided ValidationExecutor.
 */
public class DefaultFactory<T> extends Factory<T> {
    private final ValidationExecutor validationExecutor;

    protected DefaultFactory(ValidationExecutor validationExecutor, Assembler<T> assembler) {
        super(assembler);
        this.validationExecutor = validationExecutor;
    }

    @Override
    protected List<FieldError> validate() {
        return validationExecutor.validate();
    }
}
