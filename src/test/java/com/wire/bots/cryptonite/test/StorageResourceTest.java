package com.wire.bots.cryptonite.test;

import com.wire.bots.cryptonite.App;
import com.wire.bots.cryptonite.Config;
import com.wire.bots.cryptonite.StorageService;
import com.wire.bots.cryptonite.client.StorageClient;
import com.wire.bots.cryptonite.resource.StorageResource;
import com.wire.bots.sdk.server.model.NewBot;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class StorageResourceTest {
    @ClassRule
    public static final DropwizardAppRule<Config> app = new DropwizardAppRule<>(App.class, "cryptonite.yaml");

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new StorageResource())
            .build();

    private final static String botId = "test";
    private static StorageService service;

    @BeforeClass
    public static void setUp() throws Exception {
        StorageClient client = new StorageClient(resources.target("storage"));
        service = new StorageService(botId, client);
    }

    @Test
    public void testStorage() throws Exception {
        NewBot newBot = new NewBot();
        newBot.id = botId;
        newBot.client = "client";
        newBot.token = "token";

        boolean b = service.saveState(newBot);
        assert b;

        NewBot state = service.getState();
        assert state.id.equals(botId);
        assert state.client != null;
        assert state.token != null;

        service.removeState();

        NewBot empty = service.getState();

        assert empty == null;
    }

    @Test
    public void testStatus() throws Exception {
        boolean status = service.status();
        assert status;
    }
}

