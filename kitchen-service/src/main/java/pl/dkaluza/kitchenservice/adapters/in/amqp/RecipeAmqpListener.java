package pl.dkaluza.kitchenservice.adapters.in.amqp;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.exceptions.ValidationException;

@Component
class RecipeAmqpListener {
    private final RecipeAmqpFacade recipeFacade;

    public RecipeAmqpListener(RecipeAmqpFacade recipeFacade) {
        this.recipeFacade = recipeFacade;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "kitchenService.onUserCreated"),
            exchange = @Exchange(name = "userService", type = ExchangeTypes.TOPIC),
            key = "user.signUp"
        )
    )
    public void onUserSignedUp(OnUserSignedUp message) throws ValidationException {
        recipeFacade.registerCook(message);
    }
}