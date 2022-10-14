package me.escoffier.redis.d;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Map;

/**
 * A service storing and retrieving session data into Redis.
 */
@ApplicationScoped
public class SessionService {

    public static final Duration TIMEOUT = Duration.ofSeconds(60);
    private final HashCommands<String, String, Integer> hashes;
    private final KeyCommands<String> keys;

    public SessionService(RedisDataSource ds) {
        hashes = ds.hash(Integer.class);
        keys = ds.key();
    }

    public int get(String sessionId, String field) {
        var value = hashes.hget(sessionId, field);
        extendExpiration(sessionId);
        return value;
    }

    public void set(String sessionId, String field, int value) {
        hashes.hset(sessionId, field, value);
        extendExpiration(sessionId);
    }

    public void delete(String sessionId) {
        keys.del(sessionId);
    }

    private void extendExpiration(String sessionId) {
        keys.pexpire(sessionId, TIMEOUT);
    }

    public void remove(String sessionId, String product) {
        hashes.hdel(sessionId, product);
        extendExpiration(sessionId);
    }

    public Map<String, Integer> getSession(String sessionId) {
        var map = hashes.hgetall(sessionId);
        extendExpiration(sessionId);
        return map;
    }
}
