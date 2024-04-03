package pl.dkaluza.userservice.domain.events;

import pl.dkaluza.userservice.domain.User;

public record SignUpEvent(User user) {
}
