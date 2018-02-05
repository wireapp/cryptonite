package com.wire.bots.cryptonite.resource;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.wire.bots.cryptonite.CryptoRepo;
import com.wire.bots.cryptonite.model.CipherMessage;
import com.wire.bots.sdk.crypto.CryptoFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
@Path("/decrypt/{botId}")
public class DecryptResource {
    private final CryptoRepo cryptoRepo;

    public DecryptResource(CryptoRepo cryptoRepo) {
        this.cryptoRepo = cryptoRepo;
    }

    @POST
    @Timed(name = "crypto.decrypt.post.time")
    @Metered(name = "crypto.decrypt.post.meter")
    @ApiOperation(value = "Decrypt")
    public Response decrypt(@ApiParam @PathParam("botId") String botId,
                            @ApiParam CipherMessage payload) throws Exception {

        CryptoFile manager = cryptoRepo.get(botId);
        String decrypt = manager.decrypt(payload.userId, payload.clientId, payload.content);
        return Response
                .ok()
                .entity(decrypt)
                .build();
    }
}
