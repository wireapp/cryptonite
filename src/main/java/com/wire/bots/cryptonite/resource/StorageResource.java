package com.wire.bots.cryptonite.resource;

import com.wire.bots.cryptonite.App;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.storage.FileStorage;
import io.dropwizard.validation.Validated;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/storage/{service}/{botId}")
public class StorageResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveState(@PathParam("service") String service,
                              @PathParam("botId") String botId,
                              @Validated NewBot newBot) throws Exception {

        FileStorage storage = getFileStorage(service, botId);
        storage.saveState(newBot);
        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getState(@PathParam("service") String service,
                             @PathParam("botId") String botId) throws Exception {

        FileStorage storage = getFileStorage(service, botId);
        if (!storage.hasState()) {
            return Response
                    .status(404)
                    .build();
        }
        NewBot state = storage.getState();
        return Response
                .ok()
                .entity(state)
                .build();
    }

    @DELETE
    public Response removeState(@PathParam("service") String service,
                                @PathParam("botId") String botId) throws Exception {

        FileStorage storage = getFileStorage(service, botId);
        boolean removed = storage.removeState();
        return Response
                .ok(removed)
                .build();
    }

    private FileStorage getFileStorage(String service, String botId) {
        String path = String.format("%s/%s/storage/", App.configuration.path, service);
        return new FileStorage(path, botId);
    }
}
