package com.util.msa_frontend.bizservice;

import com.ibm.cardinal.util.CardinalException;
import com.ibm.cardinal.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.net.URI;
import java.util.Map;

@Component
public class BizApiClient {
    private static final Logger klu__logger = LoggerFactory.getLogger(BizApiClient.class);

    @Value("${api.gateway.url}")
    private String gatewayUrl;
    private Client klu__client;

    @Value("#{config['spring.jwt.access.header']}")
    private String accessTokenHeader;

    @Value("#{config['spring.jwt.refresh.header']}")
    private String refreshTokenHeader;

    @PostConstruct
    public void init() {
        klu__logger.info("gatewayUrl:: " + gatewayUrl);
        klu__client = ClientBuilder.newClient();
    }

    private String sendApi(BizServiceInfo serviceInfo, String path, Form form) throws Exception {
        String accessToken = "";
        try {
            Authentication authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
                    .getAuthentication();
            if (authentication != null) {
                Map<String, Object> detail = (Map<String, Object>) authentication.getDetails();
                accessToken = (String) detail.get("accessToken");

            }
        } catch (Exception e) {
            klu__logger.info(e.getMessage());
        }

        String bizPath = serviceInfo.getBizPath();
        String serviceName = serviceInfo.getServiceName();

        String serviceUri = gatewayUrl;
        if (!serviceUri.endsWith("/")) {
            serviceUri += "/";
        }
        serviceUri += bizPath + "/" + serviceName + "Service";
        try {
            URI uri = URI.create(serviceUri);
        } catch (Exception e) {
            throw new RuntimeException("Invalid URI , " +
                    "service " + serviceUri + ": " + serviceUri, e);
        }

        klu__logger.info("[" + serviceName + "] Calling service " + serviceUri +
                "/" + path + " with form: " + form.asMap());
        Response svc_response;
        try {
            svc_response = klu__client.target(serviceUri)
                    .path(path)
                    .request(MediaType.APPLICATION_JSON)
                    .header(accessTokenHeader, accessToken)
                    .post(Entity.form(form), Response.class);
        } catch (WebApplicationException wae) {
            java.lang.Throwable cause = wae.getCause();
            klu__logger.warn("[" + serviceName + "] Exception thrown in service call: " + wae.getMessage());
            if (wae.getResponse().getStatus() == CardinalException.APPLICATION_EXCEPTION) {
                klu__logger.warn("[" + serviceName + "] Re-throwing wrapped application exception: ");
                // typecast to declared exception types
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                }
            }
            throw (java.lang.RuntimeException) cause;
        }
        if (svc_response.getStatus() != 200) {
            String result = svc_response.readEntity(String.class);
            throw new RuntimeException(
                    serviceName + "." + path + " Error :: status [" + svc_response.getStatus() + "] " + result);
        }

        String response_json_str = svc_response.readEntity(String.class);
        klu__logger.info("[" + serviceName + "] Response JSON string: " + response_json_str);
        JsonReader json_reader = Json.createReader(new StringReader(response_json_str));
        JsonObject response_json = json_reader.readObject();
        String response = response_json.getString("return_value");

        return response;
    }

    /*
     * 다건 parameter
     */
    public String requestApiParams(BizServiceInfo serviceInfo, String path, Object... objects) throws Exception {
        Form form = new Form();

        // convert physical/proxy object(s) referenced by "map" to reference ID(s)
        String map_fpar = SerializationUtil.encodeWithDynamicTypeCheck(objects);
        form.param("arr", map_fpar);

        return sendApi(serviceInfo, path, form);
    }

    /*
     * 다건 parameter, return type
     */
    public <T> T requestApiParams(BizServiceInfo serviceInfo, String path, Class<T> returnClass, Object... objects)
            throws Exception {
        String res = requestApiParams(serviceInfo, path, objects);
        return (T) SerializationUtil.decodeWithDynamicTypeCheck(res);

    }

    /*
     * 단건 parameter
     */
    public String requestApi(BizServiceInfo serviceInfo, String path, Object object) throws Exception {
        Form form = new Form();

        // convert physical/proxy object(s) referenced by "map" to reference ID(s)
        String map_fpar = SerializationUtil.encodeWithDynamicTypeCheck(object);
        form.param("map", map_fpar);

        return sendApi(serviceInfo, path, form);
    }

    /*
     * 단건 parameter, return type
     */
    public <T> T requestApi(BizServiceInfo serviceInfo, String path, Object object, Class<T> returnClass)
            throws Exception {
        String res = requestApi(serviceInfo, path, object);
        return (T) SerializationUtil.decodeWithDynamicTypeCheck(res);

    }
}
