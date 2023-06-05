package com.per.msa_common.config;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();
        log.error("API error", exception);
        Map<String, String> entity = new HashMap<>();
        entity.put("error_message", exception.getMessage());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(entity).build();
    }
}
