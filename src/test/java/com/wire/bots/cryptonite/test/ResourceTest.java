package com.wire.bots.cryptonite.test;

import com.wire.bots.cryptonite.App;
import com.wire.bots.cryptonite.Config;
import com.wire.bots.cryptonite.CryptoRepo;
import com.wire.bots.cryptonite.CryptoService;
import com.wire.bots.cryptonite.client.CryptoClient;
import com.wire.bots.cryptonite.resource.DecryptResource;
import com.wire.bots.cryptonite.resource.EncryptDevicesResource;
import com.wire.bots.cryptonite.resource.EncryptPrekeysResource;
import com.wire.bots.cryptonite.resource.StatusResource;
import com.wire.bots.sdk.crypto.Crypto;
import com.wire.bots.sdk.models.otr.Missing;
import com.wire.bots.sdk.models.otr.PreKey;
import com.wire.bots.sdk.models.otr.PreKeys;
import com.wire.bots.sdk.models.otr.Recipients;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ResourceTest {
    @ClassRule
    public static final DropwizardAppRule<Config> app = new DropwizardAppRule<>(App.class, "cryptonite.yaml");
    private final static String bobId = "bob";
    private final static String bobClientId = "bob_device";
    private final static String aliceId = "alice";
    private final static String aliceClientId = "alice_device";
    private static CryptoRepo cryptoRepo = new CryptoRepo();
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DecryptResource(cryptoRepo))
            .addResource(new EncryptDevicesResource(cryptoRepo))
            .addResource(new EncryptPrekeysResource(cryptoRepo))
            .addResource(new StatusResource())
            .build();
    private static Crypto alice;
    private static Crypto bob;
    private static PreKeys bobKeys;
    private static PreKeys aliceKeys;

    @BeforeClass
    public static void setUp() throws Exception {
        CryptoClient client = new CryptoClient(resources.target(""));
        alice = new CryptoService(aliceId, client);
        bob = new CryptoService(bobId, client);

        ArrayList<PreKey> preKeys = client.newPreKeys(bobId, 0, 1);
        bobKeys = getPreKeys(preKeys, bobClientId, bobId);

        preKeys = client.newPreKeys(aliceId, 0, 1);
        aliceKeys = getPreKeys(preKeys, aliceClientId, aliceId);
    }

    private static PreKeys getPreKeys(ArrayList<PreKey> array, String clientId, String userId) {
        HashMap<String, PreKey> devs = new HashMap<>();
        for (PreKey key : array) {
            devs.put(clientId, key);
            System.out.printf("%s, %s, keyId: %s, prekey: %s\n", userId, clientId, key.id, key.key);
        }

        PreKeys keys = new PreKeys();
        keys.put(userId, devs);
        return keys;
    }

    @Test
    public void testAliceToBob() throws Exception {
        String text = "Hello Bob, This is Alice!";
        byte[] textBytes = text.getBytes();

        // Encrypt using prekeys
        Recipients encrypt = alice.encrypt(bobKeys, textBytes);

        String base64Encoded = encrypt.get(bobId, bobClientId);
        System.out.printf("Alice -> (%s,%s) cipher: %s\n", bobId, bobClientId, base64Encoded);

        // Decrypt using initSessionFromMessage
        byte[] decrypt = bob.decrypt(aliceId, aliceClientId, base64Encoded);
        String text2 = new String(decrypt);

        boolean equals = Arrays.equals(decrypt, textBytes);
        assert equals;
        assert text.equals(text2);
    }

    @Test
    public void testBobToAlice() throws Exception {
        String text = "Hello Alice, This is Bob!";
        byte[] textBytes = text.getBytes();

        Recipients encrypt = bob.encrypt(aliceKeys, textBytes);

        String base64Encoded = encrypt.get(aliceId, aliceClientId);
        System.out.printf("Bob -> (%s,%s) cipher: %s\n", aliceId, aliceClientId, base64Encoded);

        // Decrypt using initSessionFromMessage
        byte[] decrypt = alice.decrypt(bobId, bobClientId, base64Encoded);
        String text2 = new String(decrypt);

        boolean equals = Arrays.equals(decrypt, textBytes);
        assert equals;

        assert text.equals(text2);
    }

    @Test
    public void testSessions() throws Exception {
        String text = "Hello Alice, This is Bob, again!";
        byte[] textBytes = text.getBytes();

        Missing devices = new Missing();
        devices.add(aliceId, aliceClientId);
        Recipients encrypt = bob.encrypt(devices, textBytes);

        String base64Encoded = encrypt.get(aliceId, aliceClientId);
        System.out.printf("Bob -> (%s,%s) cipher: %s\n", aliceId, aliceClientId, base64Encoded);

        // Decrypt using session
        byte[] decrypt = alice.decrypt(bobId, bobClientId, base64Encoded);
        String text2 = new String(decrypt);

        boolean equals = Arrays.equals(decrypt, textBytes);
        assert equals;

        assert text.equals(text2);
    }

    @Test
    public void testGetLastPreKey() throws Exception {
        PreKey lastPreKey1 = alice.newLastPreKey();
        PreKey lastPreKey2 = bob.newLastPreKey();

        assert lastPreKey1 != null;
        assert lastPreKey2 != null;
    }

    @Test
    public void testStatus() {
        Response response = resources.target("/").request().get();
        assert response.getStatus() == 200;
    }
}

