package me.escoffier.redis.e;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.KeyValue;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import me.escoffier.redis.supes.FightSimulator;
import me.escoffier.redis.supes.SimulationRequest;
import me.escoffier.redis.supes.SimulationResult;
import me.escoffier.redis.supes.Supes;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Random;

@ApplicationScoped
public class SimulatorA extends Simulator implements Runnable {



    public SimulatorA() {
        super("a");
    }

    public void start(@Observes StartupEvent ev, RedisDataSource ds) {
        queue = ds.list(SimulationRequest.class);
        publisher = ds.pubsub(SimulationResult.class);
        new Thread(this).start();
    }

    public void stop(@Observes ShutdownEvent ev) {
        stopped = true;
    }

}
