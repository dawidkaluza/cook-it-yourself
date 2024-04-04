package pl.dkaluza.userservice.adapters.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("users")
record UserEntity(@Id Long id, String email, char[] encodedPassword, String name) {
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof UserEntity that)) {
            return false;
        }

        return id() != null && Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
