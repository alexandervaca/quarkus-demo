package org.demo.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.demo.rest.HelpersResponse;
import io.quarkus.security.AuthenticationFailedException;

@Provider
@Priority(Priorities.AUTHENTICATION + 1)
@Slf4j
public class AuthenticationFailedExceptionMapper implements ExceptionMapper<AuthenticationFailedException> {

    @Override
    public Response toResponse(AuthenticationFailedException exception) {
        log.error("Fallo de autenticacion: {}", exception.getCause().getMessage());

        return HelpersResponse.buildErrorResponse(Response.Status.UNAUTHORIZED,
                "Autenticacion fallida: " + exception.getCause().getMessage());
    }
}
