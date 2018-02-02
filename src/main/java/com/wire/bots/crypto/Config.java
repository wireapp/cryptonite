package com.wire.bots.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public class Config extends Configuration {
    @JsonProperty
    @NotNull
    public String path;

    public String getPath() {
        return path;
    }
}
