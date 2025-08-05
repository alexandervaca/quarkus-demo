package org.demo.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.demo.repository.entity.UserEntity;
import org.demo.rest.dto.UserDTO;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    public Uni<Optional<UserDTO>> findByUsername(String username) {
        return find("username", username)
                .firstResult()
                .map(entity -> {
                    if (entity == null) return Optional.empty();
                    return Optional.of(entity.toDTO());
                });
    }

}
