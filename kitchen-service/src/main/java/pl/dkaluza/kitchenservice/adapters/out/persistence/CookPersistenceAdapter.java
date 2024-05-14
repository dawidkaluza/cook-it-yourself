package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.Cook;
import pl.dkaluza.kitchenservice.ports.out.CookRepository;

@Component
class CookPersistenceAdapter implements CookRepository {
    private final CookEntityRepository cookRepository;

    public CookPersistenceAdapter(CookEntityRepository cookRepository) {
        this.cookRepository = cookRepository;
    }

    @Override
    public Cook saveCook(Cook cook) {
        Assertions.assertArgument(cook != null, "Cook is null");
        var cookId = cook.getId();
        var cookEntity = new CookEntity(cookId.getId());
        cookEntity = cookRepository.save(cookEntity);

        try {
            return Cook.fromPersistence(cookEntity.id()).produce();
        } catch (ValidationException e) {
            throw new IllegalStateException(
                "Caught unexpected validation exception. " +
                    "Make sure that data retrieved from persistence layer is valid.",
                e
            );
        }
    }
}
