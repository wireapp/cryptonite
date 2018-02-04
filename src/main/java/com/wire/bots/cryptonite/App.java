package com.wire.bots.cryptonite;

import com.wire.bots.cryptonite.resource.*;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

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
        environment.jersey().register(new DbResource());
        environment.jersey().register(new StatusResource());
    }
}