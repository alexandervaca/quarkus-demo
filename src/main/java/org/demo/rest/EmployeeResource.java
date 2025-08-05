package org.demo.rest;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.demo.rest.dto.EmployeeDTO;
import org.demo.service.EmployeeService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@RolesAllowed({"user"})
public class EmployeeResource {

    @Inject
    EmployeeService employeeService;

    @GET
    @Operation(summary = "Get all employees with update timestamp")
    public Uni<Response> list() {
        log.info("list - fetching all employees");

        return employeeService.findAll()
                .onItem().transform(HelpersResponse::buildOkResponse)
                .onFailure().recoverWithItem(this::buildInternalError);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get employee by ID, including subordinate count")
    public Uni<Response> getById(
            @Parameter(description = "ID of the employee", required = true) @PathParam("id") Long id) {

        log.info("getById - Received request for employee with id: {}", id);

        return employeeService.findById(id)
                .onItem().transform(employeeOpt -> {
                    if (employeeOpt.isEmpty()) {
                        log.warn("getById - Employee not found with id: {}", id);
                        return HelpersResponse.buildErrorResponse(Response.Status.NOT_FOUND, "Employee not found with id: " + id);
                    }

                    log.info("getById - Employee found with id: {}", id);
                    return HelpersResponse.buildOkResponse(employeeOpt.get());
                })
                .onFailure().invoke(e -> log.error("getById - Error: {}", e.getMessage(), e))
                .onFailure().recoverWithItem(
                        HelpersResponse.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                                "Unexpected error occurred while retrieving employee.")
                );
    }

    @POST
    @Operation(summary = "Register a new employee")
    @RequestBody(description = "Employee data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = EmployeeDTO.class),
                    examples = @ExampleObject(name = "NewEmployee",
                            value = "{ \"name\": \"Ana Lopez\", \"email\": \"ana.lopez@example.com\", \"supervisorId\": 1 }")))
    public Uni<Response> create(@Valid EmployeeDTO dto) {
        log.info("create - Received: {}", dto);

        return employeeService.create(dto)
                .onItem().transform(emp -> {
                    log.info("create - Created employee with id: {}", emp.getId());
                    return HelpersResponse.buildOkResponse(emp, Response.Status.CREATED);
                })
                .onFailure().invoke(e -> log.error("create - Error: {}", e.getMessage(), e))
                .onFailure().recoverWithItem(
                        HelpersResponse.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error creating employee")
                );
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing employee")
    @RequestBody(description = "Employee data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = EmployeeDTO.class),
                    examples = @ExampleObject(name = "UpdateEmployee",
                            value = "{ \"name\": \"Ana Lopez\", \"email\": \"ana.lopez@example.com\", \"supervisorId\": 1 }")))
    public Uni<Response> update(
            @Parameter(description = "ID of the employee to update", required = true) @PathParam("id") Long id,
            @Valid EmployeeDTO dto) {

        return employeeService.update(id, dto)
                .onItem().transform(opt -> {
                    if (opt.isEmpty()) {
                        log.warn("update - Employee not found with id: {}", id);
                        return HelpersResponse.buildErrorResponse(Response.Status.NOT_FOUND, "Employee not found with id: " + id);
                    }
                    log.info("update - Updated employee with id: {}", id);
                    return HelpersResponse.buildOkResponse(opt.get());
                })
                .onFailure().invoke(e -> log.error("update - Error: {}", e.getMessage(), e))
                .onFailure().recoverWithItem(
                        HelpersResponse.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error updating employee")
                );
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an employee by ID")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return employeeService.deleteById(id)
                .onItem().transform(deleted -> {
                    if (!deleted) {
                        log.warn("delete - Employee not found with id: {}", id);
                        return HelpersResponse.buildErrorResponse(Response.Status.NOT_FOUND, "Employee not found with id: " + id);
                    }
                    log.info("delete - Deleted employee with id: {}", id);
                    return HelpersResponse.buildOkResponse("Employee with id " + id + " deleted");
                })
                .onFailure().invoke(e -> log.error("delete - Error: {}", e.getMessage(), e))
                .onFailure().recoverWithItem(
                        HelpersResponse.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error deleting employee")
                );
    }

    private Response buildInternalError(Throwable e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return HelpersResponse.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected internal server error");
    }
}
