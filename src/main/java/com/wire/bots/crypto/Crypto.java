package com.wire.bots.crypto;

import com.wire.bots.sdk.OtrManager;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class Crypto {
    private ConcurrentHashMap<String, OtrManager> otr = new ConcurrentHashMap<>();

    public OtrManager get(String botId) {
        return otr.computeIfAbsent(botId, k -> {
            try {
                String cryptoDir = toPath(botId);
                File dir = new File(cryptoDir);
                dir.mkdirs();
                return new OtrManager(cryptoDir);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private static String toPath(String botId) {
        return String.format("%s/%s", App.configuration.getPath(), botId);
    }
}
