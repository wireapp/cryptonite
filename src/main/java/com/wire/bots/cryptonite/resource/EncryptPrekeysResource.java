package com.wire.bots.cryptonite.resource;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.wire.bots.cryptonite.CryptoRepo;
import com.wire.bots.cryptonite.model.PrekeysMessage;
import com.wire.bots.sdk.crypto.CryptoFile;
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
@Path("/encrypt/prekeys/{service}/{botId}")
public class EncryptPrekeysResource {
    private final CryptoRepo cryptoRepo;

    public EncryptPrekeysResource(CryptoRepo cryptoRepo) {
        this.cryptoRepo = cryptoRepo;
    }

    @POST
    @Timed(name = "crypto.encrypt.prekeys.post.time")
    @Metered(name = "crypto.encrypt.prekeys.post.meter")
    @ApiOperation(value = "Encrypt with PreKeys")
    public Response encrypt(@ApiParam @PathParam("service") String service,
                            @ApiParam @PathParam("botId") String botId,
                            @ApiParam PrekeysMessage payload) throws Exception {

        CryptoFile crypto = cryptoRepo.get(service, botId);
        byte[] content = Base64.getDecoder().decode(payload.content);
        Recipients encrypt = crypto.encrypt(payload.preKeys, content);
        return Response
                .ok()
                .entity(encrypt)
                .build();
    }

    @GET
    public Response getPrekeys(@ApiParam @PathParam("service") String service,
                               @ApiParam @PathParam("botId") String botId,
                               @QueryParam("from") Integer from,
                               @QueryParam("n") Integer n) throws Exception {

        CryptoFile crypto = cryptoRepo.get(service, botId);
        ArrayList<PreKey> preKeys = crypto.newPreKeys(from, n);
        return Response
                .ok()
                .entity(preKeys)
                .build();
    }

    @GET
    @Path("/last")
    public Response getLastPrekey(@ApiParam @PathParam("service") String service,
                                  @ApiParam @PathParam("botId") String botId) throws Exception {

        CryptoFile crypto = cryptoRepo.get(service, botId);
        PreKey preKey = crypto.newLastPreKey();
        return Response
                .ok()
                .entity(preKey)
                .build();
    }
}
