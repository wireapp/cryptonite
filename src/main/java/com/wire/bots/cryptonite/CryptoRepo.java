package com.wire.bots.cryptonite;

import com.wire.bots.sdk.crypto.CryptoFile;
import com.wire.bots.sdk.tools.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class CryptoRepo {
    private ConcurrentHashMap<String, CryptoFile> repo = new ConcurrentHashMap<>();

    public CryptoFile get(String botId) {
        return repo.computeIfAbsent(botId, k -> {
            try {
                return new CryptoFile(App.configuration.getPath(), botId);
            } catch (Exception e) {
                Logger.error("CryptoRepo.get: %s", e.getMessage());
                return null;
            }
        });
    }

    public void close(String botId) throws IOException {
        CryptoFile remove = repo.remove(botId);
        if (remove != null)
            remove.close();
    }
}
