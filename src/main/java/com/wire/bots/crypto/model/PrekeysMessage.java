package com.wire.bots.crypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wire.bots.sdk.models.otr.PreKeys;

public class PrekeysMessage {
    @JsonProperty
    public PreKeys preKeys;
    @JsonProperty
    public String content; //base64 encoded
}
