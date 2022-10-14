package me.escoffier.redis.e;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Multi;
import me.escoffier.redis.supes.SimulationResult;
import me.escoffier.redis.supes.SimulationRequest;
import me.escoffier.redis.supes.SupesRepository;
import org.jboss.resteasy.reactive.RestStreamElementType;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Demo of the job queue and pub/sub.
 * <p>
 * <code><pre>
 *     > mvn quarkus:dev
 *     > http :8080/session/clement
 *     > http POST :8080/session/clement product=coffee quantity:=2
 *     > http POST :8080/session/clement product=beer quantity:=1
 *     > http POST :8080/session/max product=coffee quantity:=1
 *     > http :8080/session/clement
 *     > http :8080/session/max
 * </pre></code>
 */
@Path("/fights")
public class FightResource {

    private final SupesRepository supes;
    private final FightDispatcher dispatcher;
    private final Multi<SimulationResult> fights;

    public FightResource(SupesRepository service, FightDispatcher dispatcher, ReactiveRedisDataSource ds) {
        this.supes = service;
        this.dispatcher = dispatcher;
        this.fights = ds.pubsub(SimulationResult.class).subscribe("fight-results")
                .broadcast().toAllSubscribers();
    }

    @POST
    public void fight() {
        this.dispatcher.submit(new SimulationRequest(supes.getRandomSupes(), supes.getRandomSupes()));
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<SimulationResult> fights() {
        return fights;
    }


}
