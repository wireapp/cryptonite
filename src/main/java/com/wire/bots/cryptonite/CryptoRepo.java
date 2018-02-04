package com.wire.bots.cryptonite;

import com.wire.bots.sdk.crypto.Crypto;
import com.wire.bots.sdk.crypto.CryptoFile;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class CryptoRepo {
    private ConcurrentHashMap<String, Crypto> repo = new ConcurrentHashMap<>();

    public Crypto get(String botId) {
        return repo.computeIfAbsent(botId, k -> {
            try {
                return new CryptoFile(App.configuration.getPath(), botId);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public void close(String botId) throws IOException {
        Crypto remove = repo.remove(botId);
        if (remove != null)
            remove.close();
    }
}
