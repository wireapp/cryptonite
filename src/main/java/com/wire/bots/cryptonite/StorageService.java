package com.wire.bots.cryptonite;

import com.wire.bots.cryptonite.client.StorageClient;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.storage.Storage;

public class StorageService implements Storage {
    private final String botId;
    private final StorageClient client;

    public StorageService(String botId, StorageClient client) {
        this.botId = botId;
        this.client = client;
    }

    @Override
    public NewBot getState() throws Exception {
        return client.getState(botId);
    }

    @Override
    public boolean saveState(NewBot newBot) throws Exception {
        return client.saveState(botId, newBot);
    }

    @Override
    public boolean status() throws Exception {
        return client.status();
    }

    @Override
    public boolean removeState() throws Exception {
        return client.removeState(botId);
    }

    @Override
    public String getPath() {
        return null;
    }
}
