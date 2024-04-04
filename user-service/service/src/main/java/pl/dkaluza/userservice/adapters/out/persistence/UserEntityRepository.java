package pl.dkaluza.userservice.adapters.out.persistence;

import org.springframework.data.repository.CrudRepository;

interface UserEntityRepository extends CrudRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
