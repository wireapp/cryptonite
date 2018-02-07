package com.wire.bots.cryptonite;

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
import java.util.*;

public class CryptoResourceTest {
    @ClassRule
    public static final DropwizardAppRule<Config> app = new DropwizardAppRule<>(App.class, "cryptonite.yaml");
    private final static String bobId = "bob";
    private final static String bobClientId = "bob_device";
    private final static String aliceId = "alice";
    private final static String aliceClientId = "alice_device";
    private static final String SERVICE = "test_service";
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
        CryptoClient client = new CryptoClient(SERVICE, resources.target(""));
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
        }

        PreKeys keys = new PreKeys();
        keys.put(userId, devs);
        return keys;
    }

    @Test
    public void testAliceToBob() throws Exception {
        String text = "Hello Bob, This is Alice!";
        byte[] bytes = text.getBytes();

        // Encrypt using prekeys
        Recipients encrypt = alice.encrypt(bobKeys, bytes);

        String base64Encoded = encrypt.get(bobId, bobClientId);

        // Decrypt using initSessionFromMessage
        String decrypt = bob.decrypt(aliceId, aliceClientId, base64Encoded);

        byte[] decode = Base64.getDecoder().decode(decrypt);

        assert Arrays.equals(decode, bytes);
        assert text.equals(new String(decode));
    }

    @Test
    public void testBobToAlice() throws Exception {
        String text = "Hello Alice, This is Bob!";
        byte[] bytes = text.getBytes();

        Recipients encrypt = bob.encrypt(aliceKeys, bytes);

        String base64Encoded = encrypt.get(aliceId, aliceClientId);

        // Decrypt using initSessionFromMessage
        String decrypt = alice.decrypt(bobId, bobClientId, base64Encoded);

        byte[] decode = Base64.getDecoder().decode(decrypt);

        assert Arrays.equals(decode, bytes);
        assert text.equals(new String(decode));
    }

    @Test
    public void testSessions() throws Exception {
        String text = "Hello Alice, This is Bob, again!";
        byte[] bytes = text.getBytes();

        Missing devices = new Missing();
        devices.add(aliceId, aliceClientId);
        Recipients encrypt = bob.encrypt(devices, bytes);

        String base64Encoded = encrypt.get(aliceId, aliceClientId);

        // Decrypt using session
        String decrypt = alice.decrypt(bobId, bobClientId, base64Encoded);

        byte[] decode = Base64.getDecoder().decode(decrypt);

        assert Arrays.equals(decode, bytes);
        assert text.equals(new String(decode));
    }

    @Test
    public void testAliceToBoBinary() throws Exception {
        Random random = new Random();
        byte[] bytes = new byte[128];
        random.nextBytes(bytes);

        // Encrypt using prekeys
        Recipients encrypt = alice.encrypt(bobKeys, bytes);

        String base64Encoded = encrypt.get(bobId, bobClientId);

        // Decrypt using initSessionFromMessage
        String decrypt = bob.decrypt(aliceId, aliceClientId, base64Encoded);

        byte[] decode = Base64.getDecoder().decode(decrypt);

        assert Arrays.equals(decode, bytes);
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

