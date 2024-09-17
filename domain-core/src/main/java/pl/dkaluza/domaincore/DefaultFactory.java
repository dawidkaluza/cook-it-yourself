package pl.dkaluza.domaincore;

import java.util.List;

/**
 * Factory where validation is dependent on provided ValidationExecutor.
 */
public class DefaultFactory<T> extends Factory<T> {
    private final Assembler<T> assembler;
    private final ValidationExecutor validationExecutor;

    protected DefaultFactory(Assembler<T> assembler, ValidationExecutor validationExecutor) {
        this.assembler = assembler;
        this.validationExecutor = validationExecutor;
    }

    @Deprecated
    protected DefaultFactory(ValidationExecutor validationExecutor, Assembler<T> assembler) {
        this(assembler, validationExecutor);
    }

    @Deprecated
    public static <T> DefaultFactory<T> newWithAssembler(ValidationExecutor validationExecutor, Assembler<T> assembler) {
        return new DefaultFactory<>(validationExecutor, assembler);
    }

    @Deprecated
    public static <T> DefaultFactory<T> newWithObject(ValidationExecutor validationExecutor, T object) {
        return new DefaultFactory<>(validationExecutor, () -> object);
    }

    public static Factory<?> validatingFactory(ValidationExecutor validationExecutor) {
        return new DefaultFactory<>(
            () -> { throw new UnsupportedOperationException("This is validating factory, not supposed to be used for assembling."); },
            validationExecutor
        );
    }

    @Override
    protected List<FieldError> validate() {
        return validationExecutor.validate();
    }

    @Override
    protected T assemble() {
        return assembler.assemble();
    }
}
