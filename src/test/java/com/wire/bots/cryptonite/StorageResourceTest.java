package com.wire.bots.cryptonite;

import com.wire.bots.cryptonite.client.StorageClient;
import com.wire.bots.cryptonite.resource.StorageListResource;
import com.wire.bots.cryptonite.resource.StorageResource;
import com.wire.bots.sdk.server.model.NewBot;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;

public class StorageResourceTest {
    @ClassRule
    public static final DropwizardAppRule<Config> app = new DropwizardAppRule<>(App.class, "cryptonite.yaml");

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new StorageResource())
            .addResource(new StorageListResource())
            .build();

    private static final String BOT_ID = "bob";
    private static final String SERVICE = "test_service";
    private static StorageService service;

    @BeforeClass
    public static void setUp() throws Exception {
        WebTarget storage = resources.target("storage").path(SERVICE);
        WebTarget list = resources.target("storage/list").path(SERVICE);

        StorageClient client = new StorageClient(storage, null, list);
        service = new StorageService(BOT_ID, client);
    }

    @Test
    public void testStorage() throws Exception {
        NewBot newBot = new NewBot();
        newBot.id = BOT_ID;
        newBot.client = "client";
        newBot.token = "token";

        boolean b = service.saveState(newBot);
        assert b;

        NewBot state = service.getState();
        assert state.id.equals(BOT_ID);
        assert state.client != null;
        assert state.token != null;

        ArrayList<NewBot> newBots = service.listAllStates();
        assert !newBots.isEmpty();

        boolean removeState = service.removeState();
        assert removeState;
    }
}

