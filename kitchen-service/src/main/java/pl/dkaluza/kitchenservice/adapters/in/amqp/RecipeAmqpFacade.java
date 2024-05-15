package pl.dkaluza.kitchenservice.adapters.in.amqp;

import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.Cook;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;

@Component
class RecipeAmqpFacade {
    private final KitchenService kitchenService;

    public RecipeAmqpFacade(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    void registerCook(OnUserSignedUp message) throws ValidationException {
        var cook = Cook.newCook(message.id()).produce();
        kitchenService.registerCook(cook);
    }
}
