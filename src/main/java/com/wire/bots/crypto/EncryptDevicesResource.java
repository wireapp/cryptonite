package com.wire.bots.crypto;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.wire.bots.crypto.model.DevicesMessage;
import com.wire.bots.sdk.OtrManager;
import com.wire.bots.sdk.models.otr.Recipients;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;

@Api
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/encrypt/devices/{botId}")
public class EncryptDevicesResource {
    private final Crypto crypto;

    public EncryptDevicesResource(Crypto crypto) {

        this.crypto = crypto;
    }

    @POST
    @Timed(name = "crypto.encrypt.devices.post.time")
    @Metered(name = "crypto.encrypt.devices.post.meter")
    @ApiOperation(value = "Encrypt with Devices")
    public Response encrypt(@ApiParam @PathParam("botId") String botId,
                            @ApiParam DevicesMessage payload) throws Exception {

        OtrManager manager = crypto.get(botId);
        byte[] content = Base64.getDecoder().decode(payload.content);
        Recipients encrypt = manager.encrypt(payload.missing, content);

        return Response
                .ok()
                .entity(encrypt)
                .build();
    }
}
