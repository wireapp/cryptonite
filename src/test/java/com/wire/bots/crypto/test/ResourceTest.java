package com.wire.bots.crypto.test;

import com.wire.bots.crypto.*;
import com.wire.bots.crypto.client.CryptoClient;
import com.wire.bots.sdk.models.otr.Missing;
import com.wire.bots.sdk.models.otr.PreKey;
import com.wire.bots.sdk.models.otr.PreKeys;
import com.wire.bots.sdk.models.otr.Recipients;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class ResourceTest {
    @ClassRule
    public static final DropwizardAppRule<Config> app = new DropwizardAppRule<>(App.class, "crypto.yaml");

    private static Crypto crypto = new Crypto();
    private static PreKeys bobKeys;
    private static PreKeys aliceKeys;

    private final static String bobId = "bob";
    private final static String bobClientId = "bob_device";
    private final static String aliceId = "alice";
    private final static String aliceClientId = "alice_device";

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DecryptResource(crypto))
            .addResource(new EncryptDevicesResource(crypto))
            .addResource(new EncryptPrekeysResource(crypto))
            .build();

    @Test
    public void testAliceToBob() {
        CryptoClient client = new CryptoClient(resources.target(""));

        String text = "Hello Bob, This is Alice!";
        byte[] textBytes = text.getBytes();

        String encoded = Base64.getEncoder().encodeToString(textBytes);
        Recipients encrypt = client.encrypt(aliceId, bobKeys, encoded);

        String cipher = encrypt.get(bobId, bobClientId);

        String text2 = client.decrypt(bobId, aliceId, aliceClientId, cipher);

        assert text.equals(text2);
    }

    @Test
    public void testBobToAlice() {
        CryptoClient client = new CryptoClient(resources.target(""));

        String text = "Hello Alice, This is Bob!";
        byte[] textBytes = text.getBytes();

        String encoded = Base64.getEncoder().encodeToString(textBytes);
        Recipients encrypt = client.encrypt(bobId, aliceKeys, encoded);

        String cipher = encrypt.get(aliceId, aliceClientId);

        String text2 = client.decrypt(aliceId, bobId, bobClientId, cipher);

        assert text.equals(text2);
    }

    @Test
    public void testSessions() throws Exception {
        CryptoClient client = new CryptoClient(resources.target(""));

        String text = "Hello Alice, This is Bob, again!";
        byte[] textBytes = text.getBytes();
        String encoded = Base64.getEncoder().encodeToString(textBytes);

        Missing devices = new Missing();
        devices.add(aliceId, aliceClientId);
        Recipients encrypt = client.encrypt(bobId, devices, encoded);

        String base64Encoded = encrypt.get(aliceId, aliceClientId);
        System.out.printf("Bob -> (%s,%s) cipher: %s\n", aliceId, aliceClientId, base64Encoded);

        // Decrypt using session
        String text2 = client.decrypt(aliceId, bobId, bobClientId, base64Encoded);

        boolean equals = Arrays.equals(text2.getBytes(), textBytes);
        assert equals;

        assert text.equals(text2);
    }

    @Test
    public void testGetLastPreKey(){
        CryptoClient client = new CryptoClient(resources.target(""));
        PreKey lastPreKey1 = client.getLastPreKey(aliceId);
        PreKey lastPreKey2 = client.getLastPreKey(bobId);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        CryptoClient client = new CryptoClient(resources.target(""));

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
}

