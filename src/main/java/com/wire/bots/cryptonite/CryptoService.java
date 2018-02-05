package com.wire.bots.cryptonite;

import com.wire.bots.cryptonite.client.CryptoClient;
import com.wire.bots.sdk.crypto.Crypto;
import com.wire.bots.sdk.models.otr.Missing;
import com.wire.bots.sdk.models.otr.PreKey;
import com.wire.bots.sdk.models.otr.PreKeys;
import com.wire.bots.sdk.models.otr.Recipients;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;

public class CryptoService implements Crypto {
    private final String botId;
    private final CryptoClient client;

    public CryptoService(String botId, URI uri) {
        this.botId = botId;
        this.client = new CryptoClient(uri);
    }

    CryptoService(String botId, CryptoClient client) {
        this.botId = botId;
        this.client = client;
    }

    @Override
    public PreKey newLastPreKey() throws Exception {
        return client.newLastPreKey(botId);
    }

    @Override
    public ArrayList<PreKey> newPreKeys(int from, int count) throws Exception {
        return client.newPreKeys(botId, from, count);
    }

    @Override
    public Recipients encrypt(PreKeys preKeys, byte[] content) throws Exception {
        String encoded = Base64.getEncoder().encodeToString(content);
        return client.encrypt(botId, preKeys, encoded);
    }

    @Override
    public Recipients encrypt(Missing missing, byte[] content) throws Exception {
        if (missing.isEmpty())
            return new Recipients();
        String encoded = Base64.getEncoder().encodeToString(content);
        return client.encrypt(botId, missing, encoded);
    }

    @Override
    public byte[] decrypt(String userId, String clientId, String cypher) throws Exception {
        return client.decrypt(botId, userId, clientId, cypher).getBytes();
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
