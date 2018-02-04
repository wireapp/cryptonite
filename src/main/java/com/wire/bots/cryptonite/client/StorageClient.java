package com.wire.bots.cryptonite.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.wire.bots.sdk.server.model.NewBot;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class StorageClient {
    private static final String STORAGE = "storage";
    private static final String DB = "db";
    private static final String FILENAME = "filename";
    private final WebTarget target;

    public StorageClient(WebTarget target) {
        this.target = target;
    }

    public StorageClient(String uri) {
        ClientConfig cfg = new ClientConfig(JacksonJsonProvider.class);
        target = JerseyClientBuilder
                .createClient(cfg)
                .target(uri);
    }

    public boolean saveState(String botId, NewBot newBot) {
        Response response = target.
                path(STORAGE).
                path(botId).
                request(MediaType.APPLICATION_JSON).
                post(Entity.entity(newBot, MediaType.APPLICATION_JSON));

        return response.getStatus() == 200;
    }

    public NewBot getState(String botId) {
        Response response = target.
                path(STORAGE).
                path(botId).
                request(MediaType.APPLICATION_JSON).
                get();

        if (response.getStatus() != 200)
            return null;

        return response.readEntity(NewBot.class);
    }

    public boolean status() {
        Response response = target.
                request(MediaType.APPLICATION_JSON).
                get();
        return response.getStatus() == 200;
    }

    public boolean removeState(String botId) {
        Response delete = target.
                path(STORAGE).
                path(botId).
                request(MediaType.APPLICATION_JSON).
                delete();

        return delete.getStatus() == 200;
    }

    public boolean saveFile(String service, String botId, String filename, String content) {
        Response response = target.
                path(DB).
                path(service).
                path(botId).
                queryParam(FILENAME, filename).
                request().
                post(Entity.entity(content, MediaType.TEXT_PLAIN));

        return response.getStatus() == 200;
    }

    public String readFile(String service, String botId, String filename) {
        Response response = target.
                path(DB).
                path(service).
                path(botId).
                queryParam(FILENAME, filename).
                request().
                get();

        if (response.getStatus() != 200)
            return null;

        return response.readEntity(String.class);
    }

    public boolean deleteFile(String service, String botId, String filename) {
        Response delete = target.
                path(DB).
                path(service).
                path(botId).
                queryParam(FILENAME, filename).
                request().
                delete();

        return delete.getStatus() == 200;
    }
}
