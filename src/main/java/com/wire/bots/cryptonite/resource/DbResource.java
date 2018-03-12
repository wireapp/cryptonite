package com.wire.bots.cryptonite.resource;

import com.wire.bots.cryptonite.App;
import com.wire.bots.sdk.storage.FileStorage;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/db/{service}/{botId}")
public class DbResource {
    @POST
    public Response saveFile(@PathParam("service") String service,
                             @PathParam("botId") String botId,
                             @NotNull @QueryParam("filename") String filename,
                             String payload) throws Exception {

        FileStorage storage = getFileStorage(service, botId);
        storage.saveFile(filename, payload);
        return Response
                .ok()
                .build();
    }

    @GET
    public Response getFile(@PathParam("service") String service,
                            @PathParam("botId") String botId,
                            @NotNull @QueryParam("filename") String filename) throws Exception {

        FileStorage storage = getFileStorage(service, botId);
        if (!storage.hasFile(filename)) {
            return Response
                    .status(404)
                    .entity("Cannot find file: " + filename)
                    .build();
        }

        String content = storage.readFile(filename);
        return Response
                .ok()
                .entity(content)
                .build();
    }

    @DELETE
    public Response deleteFile(@PathParam("service") String service,
                               @PathParam("botId") String botId,
                               @NotNull @QueryParam("filename") String filename) throws Exception {
        FileStorage storage = getFileStorage(service, botId);
        boolean removed = storage.deleteFile(filename);
        if (removed)
            return Response
                    .ok()
                    .build();

        return Response
                .status(409)
                .entity("Could not delete file")
                .build();
    }

    private FileStorage getFileStorage(String service, String botId) {
        String path = String.format("%s/%s/db/", App.configuration.path, service);
        return new FileStorage(path, botId);
    }
}
