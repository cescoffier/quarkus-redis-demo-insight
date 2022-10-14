package me.escoffier.redis.a;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Demo of the low-level API.
 * <p>
 * <code><pre>
 *     > mvn quarkus:dev
 *     > http :8080/low => 204
 *     > http POST :8080/low --raw "something"
 *     > http :8080/low => "something"
 * </pre></code>
 */
@Path("/low")
public class LowLevelClientResource {

    private final Redis redis;

    public LowLevelClientResource(Redis redis) {
        this.redis = redis;
    }


    @GET
    public Uni<String> get() {
        return redis.send(Request.cmd(Command.GET).arg("key-a"))
                .map(r -> {
                    if (r == null) {
                        return null;
                    }
                    return r.toString();
                });
    }

    @POST
    public Uni<Void> post(String val) {
        return redis.send(Request.cmd(Command.SET).arg("key-a").arg(val))
                .replaceWithVoid();
    }


}
