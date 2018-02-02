package com.wire.bots.crypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CipherMessage {
    @JsonProperty
    public String userId;
    @JsonProperty
    public String clientId;
    @JsonProperty
    public String content; //base64 encoded, encrypted cipher

}
