package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("cook")
class CookEntity implements Persistable<Long> {
    @Id
    private final Long id;

    @Transient
    private final boolean isNew;

    private CookEntity(Long id, boolean isNew) {
        this.id = id;
        this.isNew = isNew;
    }

    public static CookEntity newCook(Long id) {
        return new CookEntity(id, true);
    }

    @PersistenceCreator
    public static CookEntity fromPersistence(Long id) {
        return new CookEntity(id, false);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
