package pl.dkaluza.kitchenservice.adapters.out.persistence;

public class InMemoryCookPersistenceAdapter extends CookPersistenceAdapter {
    public InMemoryCookPersistenceAdapter() {
        this(true);
    }

    public InMemoryCookPersistenceAdapter(boolean empty) {
        super(inMemoryCookRepository(empty));
    }

    private static InMemoryCookEntityRepository inMemoryCookRepository(boolean empty) {
        var repository = new InMemoryCookEntityRepository();
        if (empty) {
            repository.deleteAll();
        }

        return repository;
    }
}
