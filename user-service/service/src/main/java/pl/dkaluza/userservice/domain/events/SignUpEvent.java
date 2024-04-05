package pl.dkaluza.userservice.domain.events;

import pl.dkaluza.userservice.domain.UserId;

public record SignUpEvent(UserId id) {
}
