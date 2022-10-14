package me.escoffier.redis.b;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Demo of the data source API.
 * <p>
 * <code><pre>
 *     > mvn quarkus:dev
 *     > http :8080/ds => 204
 *     > http POST :8080/ds --raw "something"
 *     > http :8080/ds => "something"
 * </pre></code>
 */
@Path("/ds")
public class HelloDataSource {

    private final ValueCommands<String, String> values;

    public HelloDataSource(RedisDataSource ds) {
        this.values = ds.value(String.class);
    }

    @GET
    public String get() {
        return values.get("key-b");
    }

    @POST
    public void post(String val) {
        values.set("key-b", val);
    }


}
