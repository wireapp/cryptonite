package com.wire.bots.cryptonite;

import com.wire.bots.sdk.crypto.CryptoFile;
import com.wire.bots.sdk.tools.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class CryptoRepo {
    private ConcurrentHashMap<String, CryptoFile> repo = new ConcurrentHashMap<>();

    public CryptoFile get(String service, String botId) {
        return repo.computeIfAbsent(botId, k -> {
            try {
                String path = getPath(service);
                return new CryptoFile(path, botId);
            } catch (Exception e) {
                Logger.error("CryptoRepo.get: %s", e.getMessage());
                return null;
            }
        });
    }

    private String getPath(String service) {
        return String.format("%s/%s/cryptobox", App.configuration.path, service);
    }

    public void close(String botId) throws IOException {
        CryptoFile remove = repo.remove(botId);
        if (remove != null)
            remove.close();
    }
}
