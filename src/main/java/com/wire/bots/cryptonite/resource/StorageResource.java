package com.wire.bots.cryptonite.resource;

import com.wire.bots.cryptonite.App;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.storage.FileStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/storage")
public class StorageResource {
    @POST
    @Path("{botId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveState(@PathParam("botId") String botId, NewBot newBot) throws Exception {
        FileStorage storage = new FileStorage(App.configuration.path, botId);
        storage.saveState(newBot);
        return Response
                .ok()
                .build();
    }

    @GET
    @Path("{botId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getState(@PathParam("botId") String botId) throws Exception {
        FileStorage storage = new FileStorage(App.configuration.path, botId);
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
    @Path("{botId}")
    public Response removeState(@PathParam("botId") String botId) throws Exception {
        FileStorage storage = new FileStorage(App.configuration.path, botId);
        boolean removed = storage.removeState();
        return Response
                .ok(removed)
                .build();
    }
}
