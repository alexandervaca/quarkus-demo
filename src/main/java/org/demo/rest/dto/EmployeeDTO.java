package org.demo.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Employee Data Transfer Object")
public class EmployeeDTO {

    @Schema(description = "Unique identifier of the employee", example = "1")
    private Long id;

    @NotBlank(message = "Name is required")
    @Schema(description = "Employee's full name", example = "Juan Perez", required = true)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    @Schema(description = "Employee's email address", example = "juan.perez@example.com", required = true)
    private String email;

    @Schema(description = "ID of the employee's supervisor", example = "2")
    private Long supervisorId;

    @Schema(description = "Date and time of the last update", example = "2025-06-19T20:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Number of direct subordinates", example = "3")
    private Long subordinateCount;
}
