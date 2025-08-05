package org.demo.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.demo.repository.entity.EmployeeEntity;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<EmployeeEntity> {

    public Uni<Long> countBySupervisorId(Long supervisorId) {
        return count("supervisor.id", supervisorId);
    }

    public Uni<Boolean> deleteByIdIfExists(Long id) {
        return findById(id)
                .onItem().ifNotNull().transformToUni(entity -> delete(entity).replaceWith(true))
                .onItem().ifNull().continueWith(false);
    }
}
