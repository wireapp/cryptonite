package com.wire.bots.cryptonite;

import com.wire.bots.sdk.crypto.Crypto;
import com.wire.bots.sdk.crypto.CryptoFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class CryptoRepo {
    private ConcurrentHashMap<String, Crypto> repo = new ConcurrentHashMap<>();

    private static String toPath(String botId) {
        return String.format("%s/%s", App.configuration.getPath(), botId);
    }

    public Crypto get(String botId) {
        return repo.computeIfAbsent(botId, k -> {
            try {
                String cryptoDir = toPath(botId);
                File dir = new File(cryptoDir);
                dir.mkdirs();
                return new CryptoFile(cryptoDir);
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
