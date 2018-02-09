package com.wire.bots.cryptonite;

import com.wire.bots.cryptonite.client.StorageClient;
import com.wire.bots.sdk.server.model.NewBot;
import com.wire.bots.sdk.storage.Storage;

import java.net.URI;
import java.util.ArrayList;

public class StorageService implements Storage {
    private final String botId;
    private final StorageClient client;

    public StorageService(String service, String botId, URI uri) {
        this.botId = botId;
        this.client = new StorageClient(service, uri);
    }

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
    public boolean removeState() throws Exception {
        return client.removeState(botId);
    }

    @Override
    public ArrayList<NewBot> listAllStates() throws Exception {
        return client.listAllStates();
    }

    @Override
    public boolean saveFile(String filename, String content) throws Exception {
        return client.saveFile(botId, filename, content);
    }

    @Override
    public String readFile(String filename) throws Exception {
        return client.readFile(botId, filename);
    }

    @Override
    public boolean deleteFile(String filename) throws Exception {
        return client.deleteFile(botId, filename);
    }
}
