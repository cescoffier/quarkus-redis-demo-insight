package me.escoffier.redis.e;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import me.escoffier.redis.supes.SimulationRequest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FightDispatcher {

    private final ListCommands<String, SimulationRequest> queue;

    public FightDispatcher(RedisDataSource ds) {
        queue = ds.list(SimulationRequest.class);
    }

    public void submit(SimulationRequest request) {
        queue.lpush("fight-requests", request);
    }

}
