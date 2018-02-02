package com.wire.bots.crypto;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class StatusResource {
    @GET
    public Response status() {
        return Response.ok("Cryptonite").build();
    }
}
