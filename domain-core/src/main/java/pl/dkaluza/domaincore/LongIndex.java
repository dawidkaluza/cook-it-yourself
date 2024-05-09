package pl.dkaluza.domaincore;

import static pl.dkaluza.domaincore.Validator.*;

public abstract class LongIndex extends Index<Long> {
    protected LongIndex(Long id) {
        super(id);
    }

    protected static <T extends LongIndex> LongIndexFactory<T> factory(Long id, Assembler<T> assembler) {
        return new LongIndexFactory<>(id, assembler);
    }

    public static class LongIndexFactory<T extends LongIndex> extends DefaultFactory<T> {
        protected LongIndexFactory(Long id, Assembler<T> assembler) {
            this(id, assembler, "");
        }

        protected LongIndexFactory(Long id, Assembler<T> assembler, String prefix) {
            super(
                getValidationExecutor(id, prefix),
                assembler
            );
        }

        private static ValidationExecutor getValidationExecutor(Long id, String prefix) {
            return ValidationExecutor.builder()
                .withValidator(validator(id != null && id > 0L, prefix + "id", "Id must be a positive number"))
                .build();
        }
    }
}
