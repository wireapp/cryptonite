package com.wire.bots.cryptonite.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CipherMessage {
    @JsonProperty
    public String userId;
    @JsonProperty
    public String clientId;
    @JsonProperty
    public String content; //base64 encoded, encrypted cipher

}
