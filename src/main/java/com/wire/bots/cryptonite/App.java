package com.wire.bots.cryptonite;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.health.HealthCheck;
import com.wire.bots.cryptonite.resource.*;
import com.wire.bots.sdk.crypto.Crypto;
import com.wire.bots.sdk.crypto.CryptoFile;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.tools.Util;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class App extends Application<Config> {
    public static Config configuration;

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(Config configuration, Environment environment) throws Exception {
        this.configuration = configuration;

        CryptoRepo cryptoRepo = new CryptoRepo();

        environment.jersey().register(new EncryptPrekeysResource(cryptoRepo));
        environment.jersey().register(new EncryptDevicesResource(cryptoRepo));
        environment.jersey().register(new DecryptResource(cryptoRepo));
        environment.jersey().register(new StorageResource());
        environment.jersey().register(new StorageListResource());
        environment.jersey().register(new DbResource());
        environment.jersey().register(new StatusResource());

        environment.healthChecks().register("Crypto", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                Crypto crypto = new CryptoFile(configuration.path, "test");
                crypto.close();
                return Result.healthy();
            }
        });

        environment.healthChecks().register("JCEPolicy", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                byte[] otrKey = new byte[32];
                byte[] iv = new byte[16];
                byte[] data = new byte[1024];

                Random random = new Random();
                random.nextBytes(otrKey);
                random.nextBytes(iv);
                random.nextBytes(data);
                Util.encrypt(otrKey, data, iv);
                return Result.healthy();
            }
        });

        environment.metrics().register("logger.errors", (Gauge<Integer>) Logger::getErrorCount);

        JmxReporter jmxReporter = JmxReporter.forRegistry(environment.metrics())
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        jmxReporter.start();
    }
}
