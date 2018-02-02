package com.wire.bots.crypto;

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

        Crypto crypto = new Crypto();

        environment.jersey().register(new EncryptPrekeysResource(crypto));
        environment.jersey().register(new EncryptDevicesResource(crypto));
        environment.jersey().register(new DecryptResource(crypto));
    }
}
