package me.escoffier.redis.e;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import me.escoffier.redis.supes.SimulationRequest;
import me.escoffier.redis.supes.SimulationResult;
import me.escoffier.redis.supes.Supes;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Random;

public class Simulator implements Runnable {


    ListCommands<String, SimulationRequest> queue;
    PubSubCommands<SimulationResult> publisher;
    private Logger log;
    protected volatile boolean stopped;
    private final Random random = new Random();

    Simulator(String name) {
        log = Logger.getLogger("simulator-" + name);
    }


    @Override
    public void run() {
        log.info("Starting simulator thread");
        while (!stopped) {
            var item = queue.brpop(Duration.ofSeconds(1), "fight-requests");
            if (item != null) {
                log.infof("Running simulation: %s vs. %s", item.value.fighter1().name(), item.value.fighter2().name());
                var winner = fight(item.value.fighter1(), item.value.fighter2());
                log.infof("Simulation: %s vs. %s completed, winner is %s", item.value.fighter1().name(), item.value.fighter2().name(), winner.name());
                publisher.publish("fight-results", new SimulationResult(item.value.fighter1(), item.value().fighter2(), winner.name()));
            }
        }
    }

    private Supes fight(Supes s1, Supes s2) {
        var l1 = s1.level() + random.nextInt(50);
        var l2 = s2.level() + random.nextInt(50);
        var time = random.nextInt(5000);

        nap(time);
        if (l1 >= l2) {
            return s1;
        }
        return s2;

    }

    private static void nap(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
