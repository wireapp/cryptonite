package com.wire.bots.cryptonite;

import com.wire.bots.cryptonite.client.StorageClient;
import com.wire.bots.cryptonite.resource.DbResource;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class DbResourceTest {
    @ClassRule
    public static final DropwizardAppRule<Config> app = new DropwizardAppRule<>(App.class, "cryptonite.yaml");

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DbResource())
            .build();

    private final static String BOT_ID = "test";
    private static final String SERVICE = "service";
    private static StorageService service;

    @BeforeClass
    public static void setUp() throws Exception {
        StorageClient client = new StorageClient(resources.target(""));
        service = new StorageService(SERVICE, BOT_ID, client);
    }

    @Test
    public void testDb() throws Exception {
        String file = "file";
        String content = "this is a test";
        boolean b = service.saveFile(file, content);
        assert b;

        String readFile = service.readFile(file);
        assert readFile.equals(content);

        boolean deleteFile = service.deleteFile(file);
        assert deleteFile;
    }
}

