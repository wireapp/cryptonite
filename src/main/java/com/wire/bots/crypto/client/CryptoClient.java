package com.wire.bots.crypto.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.wire.bots.crypto.model.CipherMessage;
import com.wire.bots.crypto.model.DevicesMessage;
import com.wire.bots.crypto.model.PrekeysMessage;
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
import java.util.ArrayList;

public class CryptoClient {
    private final WebTarget target;

    public CryptoClient(WebTarget target) {
        this.target = target;
    }

    public CryptoClient(String host, int port) {
        String httpUrl = String.format("http://%s:%d", host, port);
        ClientConfig cfg = new ClientConfig(JacksonJsonProvider.class);
        target = JerseyClientBuilder
                .createClient(cfg)
                .target(httpUrl);
    }

    public Recipients encrypt(String botId, PreKeys preKeys, String content) {
        PrekeysMessage prekeysMessage = new PrekeysMessage();
        prekeysMessage.content = content;
        prekeysMessage.preKeys = preKeys;

        Response response = target.
                path("encrypt/prekeys").
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
                path(botId).
                request(MediaType.TEXT_PLAIN).
                post(Entity.entity(msg, MediaType.APPLICATION_JSON));

        return response.readEntity(String.class);
    }

    public ArrayList<PreKey> newPreKeys(String botId, int from, int n) {
        return target.
                path("encrypt/prekeys").
                path(botId).
                queryParam("from", from).
                queryParam("n", n).
                request(MediaType.APPLICATION_JSON).
                get(new GenericType<ArrayList<PreKey>>() {
                });
    }

    public PreKey getLastPreKey(String botId) {
        return target.
                path("encrypt/prekeys").
                path(botId).
                path("last").
                request(MediaType.APPLICATION_JSON).
                get(PreKey.class);
    }
}
