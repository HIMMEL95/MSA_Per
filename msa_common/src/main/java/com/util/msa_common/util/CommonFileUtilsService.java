package com.per.msa_common.util;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ibm.cardinal.util.CardinalException;
import com.ibm.cardinal.util.SerializationUtil;

@Component
@Path("/CommonFileUtilsService")
public class CommonFileUtilsService {
    private static final Logger klu__logger = LoggerFactory.getLogger(CommonFileUtilsService.class);

    @Resource(name = "commonFileUtils")
    CommonFileUtils commonFileUtils;

    @POST
    @Path("/insertFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    /*
     * FormParam [
     * Map<String, Object> map,
     * List<Map<String, Object>> fileList,
     * String fileTableName,
     * String fileColumnName
     * ]
     */
    public Response insertFile(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);
        Map<String, Object> map = (Map<String, Object>) paramArr[0];
        List<Map<String, Object>> fileList = (List<Map<String, Object>>) paramArr[1];
        String fileTableName = (String) paramArr[2];
        String fileColumnName = (String) paramArr[3];

        List<Map<String, Object>> response;

        try {
            response = commonFileUtils.insertFile(map, fileList, fileTableName, fileColumnName);
        } catch (Throwable t) {
            String msg = "Call to method insertFile() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/deleteFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFile(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        Map<String, Object> map_fpar = (Map<String, Object>) SerializationUtil.decodeWithDynamicTypeCheck(map);

        try {
            commonFileUtils.deleteFile(map_fpar);
        } catch (Throwable t) {
            String msg = "Call to method deleteFile() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(null);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/deleteFileAll")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFileAll(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        List<Map<String, Object>> map_fpar = (List<Map<String, Object>>) SerializationUtil
                .decodeWithDynamicTypeCheck(map);

        try {
            commonFileUtils.deleteFileAll(map_fpar);
        } catch (Throwable t) {
            String msg = "Call to method deleteFile() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(null);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/downloadFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadFile(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        Map<String, Object> map_fpar = (Map<String, Object>) SerializationUtil.decodeWithDynamicTypeCheck(map);

        Map<String, Object> response;

        try {
            response = commonFileUtils.downloadFile(map_fpar);
        } catch (Throwable t) {
            String msg = "Call to method downloadFile() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/sampleDownloadFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sampleDownloadFile(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        Map<String, Object> map_fpar = (Map<String, Object>) SerializationUtil.decodeWithDynamicTypeCheck(map);

        Map<String, Object> response;

        try {
            response = commonFileUtils.sampleDownloadFile(map_fpar);
        } catch (Throwable t) {
            String msg = "Call to method sampleDownloadFile() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/sampleExcelDownloadFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sampleExcelDownloadFile(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        Map<String, Object> map_fpar = (Map<String, Object>) SerializationUtil.decodeWithDynamicTypeCheck(map);

        Map<String, Object> response;

        try {
            response = commonFileUtils.sampleExcelDownloadFile(map_fpar);
        } catch (Throwable t) {
            String msg = "Call to method sampleExcelDownloadFile() of CommonFileUtils raised exception: "
                    + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/insertPhoto")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    /*
     * FormParam [
     * Map<String, Object> map,
     * Map<String, Object> photo,
     * String fileTableName,
     * String fileColumnName
     * ]
     */
    public Response insertPhoto(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);
        Map<String, Object> map = (Map<String, Object>) paramArr[0];
        Map<String, Object> photo = (Map<String, Object>) paramArr[1];
        String fileTableName = (String) paramArr[2];
        String fileColumnName = (String) paramArr[3];

        Map<String, Object> response;

        try {
            response = commonFileUtils.insertPhoto(map, photo, fileTableName, fileColumnName);
        } catch (Throwable t) {
            String msg = "Call to method insertPhoto() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/deletePhoto")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePhoto(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        Map<String, Object> map_fpar = (Map<String, Object>) SerializationUtil.decodeWithDynamicTypeCheck(map);

        try {
            commonFileUtils.deletePhoto(map_fpar);
        } catch (Throwable t) {
            String msg = "Call to method deletePhoto() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(null);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/movePhoto")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response movePhoto(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);

        Map<String, Object> oldPhoto = (Map<String, Object>) paramArr[0];
        Map<String, Object> map = (Map<String, Object>) paramArr[1];
        Map<String, Object> newPhoto = (Map<String, Object>) paramArr[2];
        String fileTableName = (String) paramArr[3];
        String fileColumnName = (String) paramArr[4];

        Map<String, Object> response;

        try {
            response = commonFileUtils.movePhoto(oldPhoto, map, newPhoto, fileTableName, fileColumnName);
        } catch (Throwable t) {
            String msg = "Call to method movePhoto() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/downICS")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response downICS(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        Map<String, Object> map_fpar = (Map<String, Object>) SerializationUtil.decodeWithDynamicTypeCheck(map);

        Map<String, Object> response;

        try {
            commonFileUtils.downICS(map_fpar);
        } catch (Throwable t) {
            String msg = "Call to method downICS() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(true);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/deleteFileCal")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFileCal(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);
        Map<String, Object> map = (Map<String, Object>) paramArr[0];
        String calUserInfoPath = (String) paramArr[1];

        try {
            commonFileUtils.deleteFile(map, calUserInfoPath);
        } catch (Throwable t) {
            String msg = "Call to method deleteFileCal() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(true);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/getGoogleIcsEventList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGoogleIcsEventList(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);
        Map<String, Object> map = (Map<String, Object>) paramArr[0];
        String calUserInfoPath = (String) paramArr[1];

        List<Map<String, Object>> response;

        try {
            response = commonFileUtils.getGoogleIcsEventList(map, calUserInfoPath);
        } catch (Throwable t) {
            String msg = "Call to method getGoogleIcsEventList() of CommonFileUtils raised exception: "
                    + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/outIcsEventList")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response outIcsEventList(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);
        List<Map<String, Object>> list = (List<Map<String, Object>>) paramArr[0];
        Map<String, Object> map = (Map<String, Object>) paramArr[1];

        try {
            commonFileUtils.outIcsEventList(list, map);
        } catch (Throwable t) {
            String msg = "Call to method outIcsEventList() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(null);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/deleteIncorrectUserLoginInfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteIncorrectUserLoginInfo(
            @FormParam("map") String map,
            @Context HttpServletResponse servletResponse) {
        String filePath = (String) SerializationUtil.decodeWithDynamicTypeCheck(map);

        try {
            commonFileUtils.deleteIncorrectUserLoginInfo(filePath);
        } catch (Throwable t) {
            String msg = "Call to method deleteIncorrectUserLoginInfo() of CommonFileUtils raised exception: "
                    + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(null);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/readOutlookInfoFile")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readOutlookInfoFile(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);
        Map<String, Object> map = (Map<String, Object>) paramArr[0];
        List<Map<String, Object>> list = (List<Map<String, Object>>) paramArr[1];
        String calType = (String) paramArr[2];
        String userFilePath = (String) paramArr[3];

        List<Map<String, Object>> response;

        try {
            response = commonFileUtils.readOutlookInfoFile(map, list, calType, userFilePath);
        } catch (Throwable t) {
            String msg = "Call to method readOutlookInfoFile() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }

    @POST
    @Path("/outlookInfoSave")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response outlookInfoSave(
            @FormParam("arr") String arr,
            @Context HttpServletResponse servletResponse) {
        Object[] paramArr = (Object[]) SerializationUtil.decodeWithDynamicTypeCheck(arr);
        String creatorId = (String) paramArr[0];
        String outlookID = (String) paramArr[1];
        String outlookPW = (String) paramArr[2];
        String flag = (String) paramArr[3];
        String userFilepath = (String) paramArr[4];
        try {
            commonFileUtils.outlookInfoSave(creatorId, outlookID, outlookPW, flag, userFilepath);
        } catch (Throwable t) {
            String msg = "Call to method outlookInfoSave() of CommonFileUtils raised exception: " + t.getMessage();
            klu__logger.warn(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();

        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(null);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();
        klu__logger.info("[CommonFileUtils] Returning JSON object: " + jsonobj.toString());
        return Response
                .status(Response.Status.OK)
                .entity(jsonobj.toString())
                .build();
    }
}
