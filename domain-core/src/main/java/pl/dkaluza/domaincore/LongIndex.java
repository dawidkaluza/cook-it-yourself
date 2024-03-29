package pl.dkaluza.domaincore;

import java.util.function.Supplier;

public abstract class LongIndex extends Index<Long> {
    protected LongIndex(Long id) {
        super(id);
    }

    protected static <T extends LongIndex> LongIndexFactory<T> factory(Long id, Supplier<T> objectSupplier) {
        return new LongIndexFactory<>(id, objectSupplier);
    }

    public static class LongIndexFactory<T extends LongIndex> extends DefaultFactory<T> {
        protected LongIndexFactory(Long id, Supplier<T> objectSupplier) {
            super(
                ValidationExecutor.builder()
                    .withValidation(id != null && id > 0L, "id", "Id must be a positive number")
                    .build(),
                objectSupplier
            );
        }
    }
}
