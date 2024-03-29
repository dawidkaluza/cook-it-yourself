package pl.dkaluza.domaincore;

public abstract class LongIndex extends Index<Long> {
    protected LongIndex(Long id) {
        super(id);
    }

    protected static <T extends LongIndex> LongIndexFactory<T> factory(Long id, Assembler<T> assembler) {
        return new LongIndexFactory<>(id, assembler);
    }

    public static class LongIndexFactory<T extends LongIndex> extends DefaultFactory<T> {
        protected LongIndexFactory(Long id, Assembler<T> assembler) {
            super(
                ValidationExecutor.builder()
                    .withValidation(id != null && id > 0L, "id", "Id must be a positive number")
                    .build(),
                assembler
            );
        }
    }
}
