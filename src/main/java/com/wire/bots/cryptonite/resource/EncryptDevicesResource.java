package com.wire.bots.cryptonite.resource;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.wire.bots.cryptonite.CryptoRepo;
import com.wire.bots.cryptonite.model.DevicesMessage;
import com.wire.bots.sdk.crypto.Crypto;
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
    private final CryptoRepo cryptoRepo;

    public EncryptDevicesResource(CryptoRepo cryptoRepo) {
        this.cryptoRepo = cryptoRepo;
    }

    @POST
    @Timed(name = "crypto.encrypt.devices.post.time")
    @Metered(name = "crypto.encrypt.devices.post.meter")
    @ApiOperation(value = "Encrypt with Devices")
    public Response encrypt(@ApiParam @PathParam("botId") String botId,
                            @ApiParam DevicesMessage payload) throws Exception {

        Crypto manager = cryptoRepo.get(botId);
        byte[] content = Base64.getDecoder().decode(payload.content);
        Recipients encrypt = manager.encrypt(payload.missing, content);

        return Response
                .ok()
                .entity(encrypt)
                .build();
    }
}
