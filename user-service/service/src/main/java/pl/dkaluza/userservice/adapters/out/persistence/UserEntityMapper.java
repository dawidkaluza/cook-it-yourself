package pl.dkaluza.userservice.adapters.out.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.userservice.domain.User;

@Mapper
interface UserEntityMapper {
    @Mapping(source = "id.id", target = "id")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "password.value", target = "encodedPassword")
    @Mapping(source = "name.value", target = "name")
    UserEntity toEntity(User user);

    default User toDomain(UserEntity entity) throws ValidationException {
        return User.builder()
            .id(entity.id())
            .email(entity.email())
            .password(entity.encodedPassword())
            .name(entity.name())
            .fromPersistenceFactory()
            .produce();
    }
}
