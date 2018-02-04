package com.wire.bots.cryptonite.resource;

import com.wire.bots.cryptonite.App;
import com.wire.bots.sdk.storage.FileStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/db/{service}/{botId}")
public class DbResource {
    @POST
    public Response saveFile(@PathParam("service") String service,
                             @PathParam("botId") String botId,
                             @QueryParam("filename") String filename,
                             String payload) throws Exception {
        if (filename == null) {
            return Response
                    .status(400)
                    .entity("Missing filename")
                    .build();

        }

        FileStorage storage = getFileStorage(service, botId);
        storage.saveFile(filename, payload);
        return Response
                .ok()
                .build();
    }

    @GET
    public Response getFile(@PathParam("service") String service,
                            @PathParam("botId") String botId,
                            @QueryParam("filename") String filename
    ) throws Exception {
        if (filename == null) {
            return Response
                    .status(400)
                    .entity("Missing filename")
                    .build();

        }

        FileStorage storage = getFileStorage(service, botId);
        String content = storage.readFile(filename);
        return Response
                .ok()
                .entity(content)
                .build();
    }

    @DELETE
    public Response deleteFile(@PathParam("service") String service,
                               @PathParam("botId") String botId,
                               @QueryParam("filename") String filename
    ) throws Exception {
        FileStorage storage = getFileStorage(service, botId);
        boolean removed = storage.deleteFile(filename);
        return Response
                .ok(removed)
                .build();
    }

    private FileStorage getFileStorage(String service, String botId) {
        String path = String.format("%s/%s", App.configuration.path, service);
        return new FileStorage(path, botId);
    }
}
