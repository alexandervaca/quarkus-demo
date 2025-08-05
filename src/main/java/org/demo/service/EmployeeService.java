package org.demo.service;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.demo.repository.EmployeeRepository;
import org.demo.repository.entity.EmployeeEntity;
import org.demo.rest.dto.EmployeeDTO;
import org.demo.rest.dto.EmployeeListDTO;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class EmployeeService {

    @Inject
    EmployeeRepository employeeRepository;

    @WithSession
    public Uni<List<EmployeeListDTO>> findAll() {
        log.info("findAll - retrieving all employees");

        return employeeRepository.listAll()
                .map(entityList -> {
                    List<EmployeeListDTO> result = entityList.stream()
                            .map(EmployeeEntity::toListDTO)
                            .toList();

                    log.info("findAll - end. Returning {} employees DTOs", result.size());
                    return result;
                });
    }

    @WithSession
    public Uni<Optional<EmployeeDTO>> findById(Long id) {
        log.info("findById - Looking up employee with id: {}", id);

        Uni<EmployeeEntity> employeeUni = employeeRepository.findById(id);
        Uni<Long> countUni = employeeRepository.countBySupervisorId(id);

        return Uni.combine().all().unis(employeeUni, countUni).asTuple()
                .map(tuple -> {
                    EmployeeEntity entity = tuple.getItem1();
                    Long subordinateCount = tuple.getItem2();

                    if (entity == null) {
                        log.warn("findById - No employee found with id: {}", id);
                        return Optional.empty();
                    }

                    EmployeeDTO dto = entity.toDTO();
                    dto.setSubordinateCount(subordinateCount);
                    log.info("findById - Found employee with id: {}, subordinates: {}", id, subordinateCount);
                    return Optional.of(dto);
                });
    }

    @WithTransaction
    public Uni<EmployeeDTO> create(EmployeeDTO dto) {
        log.info("create - Creating employee: {}", dto);

        Uni<EmployeeEntity> supervisorUni = dto.getSupervisorId() != null
                ? employeeRepository.findById(dto.getSupervisorId())
                : Uni.createFrom().nullItem();

        return supervisorUni
                .onItem().transform(supervisor -> EmployeeEntity.builder()
                        .name(dto.getName())
                        .email(dto.getEmail())
                        .supervisor(supervisor)
                        .build())
                .onItem().transformToUni(entity ->
                        employeeRepository.persist(entity).replaceWith(entity))
                .onItem().transform(EmployeeEntity::toDTO)
                .invoke(dtoCreated -> log.info("create - Employee created with id: {}", dtoCreated.getId()))
                .onFailure().invoke(e -> log.error("create - Error persisting employee: {}", e.getMessage(), e));
    }

    //@WithTransaction
    public Uni<Optional<EmployeeDTO>> update(Long id, EmployeeDTO dto) {
        log.info("update - Updating employee with id: {}", id);

        return Panache.withTransaction(() ->
                employeeRepository.findById(id)
                        .flatMap(entity -> {
                            if (entity == null) {
                                log.warn("update - Employee not found with id: {}", id);
                                return Uni.createFrom().item(Optional.<EmployeeDTO>empty());
                            }

                            entity.setName(dto.getName());
                            entity.setEmail(dto.getEmail());

                            Uni<EmployeeEntity> supervisorUni = dto.getSupervisorId() != null
                                    ? employeeRepository.findById(dto.getSupervisorId())
                                    : Uni.createFrom().nullItem();

                            return supervisorUni
                                    .invoke(entity::setSupervisor)
                                    .replaceWith(Optional.of(entity.toDTO()));
                        })
        ).onFailure().invoke(e ->
                log.error("update - Error updating employee with id {}: {}", id, e.getMessage(), e)
        );
    }

    @WithTransaction
    public Uni<Boolean> deleteById(Long id) {
        log.info("deleteById - Attempting to delete employee with id: {}", id);

        return employeeRepository.deleteByIdIfExists(id)
                .invoke(deleted -> log.info("deleteById - Deleted status: {}", deleted));
    }
}
