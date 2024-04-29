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

    public static <T> DefaultFactory<T> newWithAssembler(ValidationExecutor validationExecutor, Assembler<T> assembler) {
        return new DefaultFactory<>(validationExecutor, assembler);
    }

    public static <T> DefaultFactory<T> newWithObject(ValidationExecutor validationExecutor, T object) {
        return new DefaultFactory<>(validationExecutor, () -> object);
    }

    @Override
    protected List<FieldError> validate() {
        return validationExecutor.validate();
    }
}
