package com.wire.bots.crypto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wire.bots.sdk.models.otr.Missing;

public class DevicesMessage {
    @JsonProperty
    public Missing missing;

    @JsonProperty
    public String content; //base64 encoded
}
