package com.wire.bots.cryptonite.resource;

import com.wire.bots.cryptonite.App;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.storage.FileStorage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/storage/list/{service}")
public class StorageListResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllStates(@PathParam("service") String service) throws Exception {
        String path = String.format("%s/%s/storage/", App.configuration.path, service);
        FileStorage storage = new FileStorage(path, "");
        ArrayList<NewBot> newBots = storage.listAllStates();
        return Response
                .ok()
                .entity(newBots)
                .build();
    }
}
