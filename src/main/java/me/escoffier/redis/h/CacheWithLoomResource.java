package me.escoffier.redis.h;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.smallrye.common.annotation.RunOnVirtualThread;
import me.escoffier.redis.supes.FightSimulator;
import me.escoffier.redis.supes.Supes;
import me.escoffier.redis.supes.SupesRepository;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Demo of the cache API with entities.
 * <p>
 * <code><pre>
 *     > mvn quarkus:dev
 *     > http POST :8080/cache-loom id1:=3 id2:=23 => take time
 *     > http POST :8080/cache-loom id1:=3 id2:=23 => immediate response
 * </pre></code>
 */
@Path("/cache-loom")
public class CacheWithLoomResource {

    @Inject
    Logger logger;

    private final SupesRepository repository;
    private final FightSimulator simulator;
    private final ValueCommands<String, Supes> cache;

    public CacheWithLoomResource(SupesRepository repository, FightSimulator simulator, RedisDataSource ds) {
        this.repository = repository;
        this.simulator = simulator;
        this.cache = ds.value(Supes.class);
    }

    public record FightRequest(int id1, int id2) {

    }

    /**
     * Compute cache key
     * Check if present, if so return
     * If not, retrieve, simulate and cache the value
     */
    @POST
    @RunOnVirtualThread
    public Supes fight(FightRequest request) {
        var key = "cache-" + request.id1 + "-" + request.id2;
        // if in cache -> retrieve
        var inCache = this.cache.get(key);
        if (inCache != null) {
            logger.info("Retrieve simulation result from the cache");
            return inCache;
        }

        // else compute:
        var s1 = repository.getByIndex(request.id1);
        var s2 = repository.getByIndex(request.id2);
        var winner = simulator.fight(s1, s2);

        // Store with TTL
        this.cache.psetex(key, 10000, winner);
        logger.infof("Cached the simulation result for %s vs. %s (winner is %s)", s1.name(), s2.name(),
                winner.name());
        return winner;
    }


}
