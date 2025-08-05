package org.demo.rest;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.demo.repository.UserRepository;
import org.demo.rest.dto.TokenResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Path("/auth")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Slf4j
public class AuthResource {

    public static final String ROLES = "user";
    @Inject
    UserRepository userRepository;

    @Inject
    @ConfigProperty(name = "jwt.duration", defaultValue = "300")
    Long jwtDuration;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "auth-server")
    String jwtIssuer;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> login(Credentials credentials) {
        log.info("Login attempt: {}", credentials.username);

        return userRepository.findByUsername(credentials.username)
                .onItem().transform(optUser -> {
                    if (optUser.isEmpty() || !BCrypt.checkpw(credentials.password, optUser.get().passwordHash)) {
                        log.warn("Invalid login for user: {}", credentials.username);

                        return HelpersResponse.buildErrorResponse(Response.Status.UNAUTHORIZED,
                                String.format("Invalid login for user: %S", credentials.username));
                    }

                    TokenResponse tokenResponse = generateToken(optUser.get().username);
                    return Response.ok(tokenResponse).build();
                })
                .onFailure().invoke(e -> log.error("login - Error: {}", e.getMessage(), e))
                .onFailure().recoverWithItem(
                        HelpersResponse.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                                "Unexpected internal server error")
                );
    }

    private TokenResponse generateToken(String username) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtDuration, ChronoUnit.SECONDS);

        String token = Jwt.issuer(jwtIssuer) // Emisor configurable
                .subject(username) // El sujeto del token es el nombre de usuario
                .groups(Set.of(ROLES)) // Roles/grupos del usuario. Esto deberia venir de la BDD
                .issuedAt(now) // Tiempo de emision
                .expiresAt(expiresAt) // Tiempo de expiracion
                .sign();
        return new TokenResponse(token, now, expiresAt);
    }

    public static class Credentials {
        public String username;
        public String password;
    }

}
