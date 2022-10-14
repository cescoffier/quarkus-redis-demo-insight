package me.escoffier.redis.f;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.sortedset.ScoredValue;
import io.quarkus.redis.datasource.sortedset.SortedSetCommands;
import io.quarkus.redis.datasource.sortedset.ZRangeArgs;
import io.smallrye.mutiny.Uni;
import me.escoffier.redis.supes.SimulationResult;
import me.escoffier.redis.supes.Supes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Demonstrate sorted sets.
 *
 */
@Path("/leaderboard")
public class LeaderboardResource {

    private final SortedSetCommands<String, Supes> leaders;


    public LeaderboardResource(RedisDataSource ds, ReactiveRedisDataSource reactiveRedisDataSource) {
        leaders = ds.sortedSet(Supes.class);
        var set = reactiveRedisDataSource.sortedSet(Supes.class);
        ds.pubsub(SimulationResult.class)
                .subscribe("fight-results", outcome -> {
                    var winner = getWinner(outcome);
                    set.zaddincr("leaderboard", 1, winner).subscribe().asCompletionStage();
                });
    }

    public record Score(Supes supes, long score) {
    }


    @GET
    public List<Score> getLeaderboard() {
        return leaders
                .zrangeWithScores("leaderboard", 0, 9, new ZRangeArgs().rev())
                .stream()
                .map(sv -> new Score(sv.value, (long) sv.score))
                .collect(Collectors.toList());
    }


    private Supes getWinner(SimulationResult outcome) {
        if (outcome.winner().equalsIgnoreCase(outcome.fighter1().name())) {
            return outcome.fighter1();
        }
        return outcome.fighter2();
    }

}
