package org.demo.repository.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;
import org.demo.rest.dto.EmployeeDTO;
import org.demo.rest.dto.EmployeeListDTO;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "employees")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeEntity extends PanacheEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private EmployeeEntity supervisor;

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    private List<EmployeeEntity> subordinates;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public EmployeeDTO toDTO() {
        return EmployeeDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .supervisorId(this.supervisor != null ? this.supervisor.id : null)
                .updatedAt(this.updatedAt)
                .build();
    }

    public EmployeeListDTO toListDTO() {
        return EmployeeListDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .supervisorId(this.supervisor != null ? this.supervisor.id : null)
                .updatedAt(this.updatedAt)
                .build();
    }
}
