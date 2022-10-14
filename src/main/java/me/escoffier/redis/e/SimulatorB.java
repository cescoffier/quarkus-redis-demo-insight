package me.escoffier.redis.e;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import me.escoffier.redis.supes.SimulationRequest;
import me.escoffier.redis.supes.SimulationResult;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class SimulatorB extends Simulator implements Runnable {

    public SimulatorB() {
        super("b");
    }

    public void start(@Observes StartupEvent ev, RedisDataSource ds) {
        queue = ds.list(SimulationRequest.class);
        publisher = ds.pubsub(SimulationResult.class);
        new Thread(this).start();
    }

    public void stop(@Observes ShutdownEvent ev) {
        stopped = true;
    }

    @PostConstruct
    public void init(RedisDataSource ds) {
        queue = ds.list(SimulationRequest.class);
        publisher = ds.pubsub(SimulationResult.class);
    }
}
