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
            this(id, assembler, "", "id");
        }

        protected LongIndexFactory(Long id, Assembler<T> assembler, String prefix) {
            this(id, assembler, prefix, "id");
        }

        // Due to backwards compatibility, having only fieldName in here (without prefix, which is quite redundant) wasn't an option.
        protected LongIndexFactory(Long id, Assembler<T> assembler, String prefix, String fieldName) {
            super(
                getValidationExecutor(id, prefix + fieldName),
                assembler
            );
        }

        private static ValidationExecutor getValidationExecutor(Long id, String fieldName) {
            return ValidationExecutor.builder()
                .withValidator(validator(id != null && id > 0L, fieldName, "Id must be a positive number"))
                .build();
        }
    }
}
