package org.demo.rest;

import jakarta.ws.rs.core.Response;
import org.demo.rest.dto.ResponseBody;
import org.demo.rest.dto.Status;

import java.time.LocalDateTime;

public class HelpersResponse {

    public static Response buildOkResponse(Object data) {
        return buildOkResponse(data, Response.Status.OK);
    }

    public static Response buildOkResponse(Object data, Response.Status status) {
        ResponseBody body = ResponseBody.builder()
                .status(Status.builder()
                        .code(status.getStatusCode())
                        .description(status.getReasonPhrase())
                        .build())
                .data(data)
                .date(LocalDateTime.now())
                .build();

        return Response.status(status).entity(body).build();
    }

    public static Response buildErrorResponse(Response.Status status, String message) {
        ResponseBody body = ResponseBody.builder()
                .status(Status.builder()
                        .code(status.getStatusCode())
                        .description(message)
                        .build())
                .date(LocalDateTime.now())
                .build();

        return Response.status(status).entity(body).build();
    }
}
