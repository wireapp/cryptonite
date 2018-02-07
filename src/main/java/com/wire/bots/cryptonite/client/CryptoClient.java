package com.wire.bots.cryptonite.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.wire.bots.cryptonite.model.CipherMessage;
import com.wire.bots.cryptonite.model.DevicesMessage;
import com.wire.bots.cryptonite.model.PrekeysMessage;
import com.wire.bots.sdk.models.otr.Missing;
import com.wire.bots.sdk.models.otr.PreKey;
import com.wire.bots.sdk.models.otr.PreKeys;
import com.wire.bots.sdk.models.otr.Recipients;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;

public class CryptoClient {
    private final WebTarget target;
    private final String service;

    public CryptoClient(String service, URI uri) {
        this.service = service;
        ClientConfig cfg = new ClientConfig(JacksonJsonProvider.class);
        target = JerseyClientBuilder
                .createClient(cfg)
                .target(uri);
    }

    public CryptoClient(String service, WebTarget target) {
        this.service = service;
        this.target = target;
    }

    public Recipients encrypt(String botId, PreKeys preKeys, String content) {
        PrekeysMessage prekeysMessage = new PrekeysMessage();
        prekeysMessage.content = content;
        prekeysMessage.preKeys = preKeys;

        Response response = target.
                path("encrypt/prekeys").
                path(service).
                path(botId).
                request(MediaType.APPLICATION_JSON).
                post(Entity.entity(prekeysMessage, MediaType.APPLICATION_JSON));

        return response.readEntity(Recipients.class);
    }

    public Recipients encrypt(String botId, Missing missing, String content) {
        DevicesMessage devicesMessage = new DevicesMessage();
        devicesMessage.content = content;
        devicesMessage.missing = missing;

        Response response = target.
                path("encrypt/devices").
                path(service).
                path(botId).
                request(MediaType.APPLICATION_JSON).
                post(Entity.entity(devicesMessage, MediaType.APPLICATION_JSON));

        return response.readEntity(Recipients.class);
    }

    public String decrypt(String botId, String userId, String clientId, String cipher) {
        CipherMessage msg = new CipherMessage();
        msg.userId = userId;
        msg.clientId = clientId;
        msg.content = cipher;

        Response response = target.
                path("decrypt").
                path(service).
                path(botId).
                request(MediaType.TEXT_PLAIN).
                post(Entity.entity(msg, MediaType.APPLICATION_JSON));

        return response.readEntity(String.class);
    }

    public ArrayList<PreKey> newPreKeys(String botId, int from, int n) {
        return target.
                path("encrypt/prekeys").
                path(service).
                path(botId).
                queryParam("from", from).
                queryParam("n", n).
                request(MediaType.APPLICATION_JSON).
                get(new GenericType<ArrayList<PreKey>>() {
                });
    }

    public PreKey newLastPreKey(String botId) {
        return target.
                path("encrypt/prekeys").
                path(service).
                path(botId).
                path("last").
                request(MediaType.APPLICATION_JSON).
                get(PreKey.class);
    }
}
