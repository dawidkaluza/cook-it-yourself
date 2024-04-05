package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("messages")
record MessageEntity(@Id Long id, String exchange, String routingKey, String message) {
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof MessageEntity that)) {
            return false;
        }

        return id() != null && Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
