package pl.dkaluza.kitchenservice.ports.out;

import pl.dkaluza.kitchenservice.domain.Cook;

public interface CookRepository {
    Cook saveCook(Cook cook);
}
