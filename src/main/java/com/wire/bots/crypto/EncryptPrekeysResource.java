package com.wire.bots.crypto;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.wire.bots.crypto.model.PrekeysMessage;
import com.wire.bots.sdk.OtrManager;
import com.wire.bots.sdk.models.otr.PreKey;
import com.wire.bots.sdk.models.otr.Recipients;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Base64;

@Api
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/encrypt/prekeys/{botId}")
public class EncryptPrekeysResource {
    private final Crypto crypto;

    public EncryptPrekeysResource(Crypto crypto) {

        this.crypto = crypto;
    }

    @POST
    @Timed(name = "crypto.encrypt.prekeys.post.time")
    @Metered(name = "crypto.encrypt.prekeys.post.meter")
    @ApiOperation(value = "Encrypt with PreKeys")
    public Response encrypt(@ApiParam @PathParam("botId") String botId,
                            @ApiParam PrekeysMessage payload) throws Exception {

        OtrManager manager = crypto.get(botId);
        byte[] content = Base64.getDecoder().decode(payload.content);
        Recipients encrypt = manager.encrypt(payload.preKeys, content);

        return Response
                .ok()
                .entity(encrypt)
                .build();
    }

    @GET
    public Response getPrekeys(@ApiParam @PathParam("botId") String botId,
                               @QueryParam("from") Integer from,
                               @QueryParam("n") Integer n) throws Exception {

        OtrManager manager = crypto.get(botId);
        ArrayList<PreKey> preKeys = manager.newPreKeys(from, n);

        return Response
                .ok()
                .entity(preKeys)
                .build();
    }

    @GET
    @Path("/last")
    public Response getLastPrekey(@ApiParam @PathParam("botId") String botId) throws Exception {

        OtrManager manager = crypto.get(botId);
        PreKey preKey = manager.newLastPreKey();

        return Response
                .ok()
                .entity(preKey)
                .build();
    }
}
