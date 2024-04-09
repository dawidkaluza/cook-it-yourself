package pl.dkaluza.userservice.adapters.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface UserEntityRepository extends CrudRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
