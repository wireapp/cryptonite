package com.wire.bots.cryptonite.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.wire.bots.sdk.server.model.NewBot;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

public class StorageClient {
    private static final String FILENAME = "filename";
    private final WebTarget storage;
    private final WebTarget db;

    public StorageClient(String service, URI uri) {
        ClientConfig cfg = new ClientConfig(JacksonJsonProvider.class);
        JerseyWebTarget target = JerseyClientBuilder
                .createClient(cfg)
                .target(uri);

        storage = target
                .path("storage")
                .path(service);
        db = target.
                path("db")
                .path(service);
    }

    // Testing only
    public StorageClient(WebTarget storage, WebTarget db) {
        this.storage = storage;
        this.db = db;
    }

    public boolean saveState(String botId, NewBot newBot) {
        Response response = storage.
                path(botId).
                request(MediaType.APPLICATION_JSON).
                post(Entity.entity(newBot, MediaType.APPLICATION_JSON));

        return response.getStatus() == 200;
    }

    public NewBot getState(String botId) {
        Response response = storage.
                path(botId).
                request(MediaType.APPLICATION_JSON).
                get();

        if (response.getStatus() != 200)
            return null;

        return response.readEntity(NewBot.class);
    }

    public boolean removeState(String botId) {
        Response delete = storage.
                path(botId).
                request(MediaType.APPLICATION_JSON).
                delete();

        return delete.getStatus() == 200;
    }

    public boolean saveFile(String botId, String filename, String content) {
        Response response = db.
                path(botId).
                queryParam(FILENAME, filename).
                request().
                post(Entity.entity(content, MediaType.TEXT_PLAIN));

        return response.getStatus() == 200;
    }

    public String readFile(String botId, String filename) {
        Response response = db.
                path(botId).
                queryParam(FILENAME, filename).
                request().
                get();

        if (response.getStatus() != 200)
            return null;

        return response.readEntity(String.class);
    }

    public boolean deleteFile(String botId, String filename) {
        Response delete = db.
                path(botId).
                queryParam(FILENAME, filename).
                request().
                delete();

        return delete.getStatus() == 200;
    }
}
