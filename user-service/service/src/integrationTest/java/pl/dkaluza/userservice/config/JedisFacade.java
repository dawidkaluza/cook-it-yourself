package pl.dkaluza.userservice.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisFacade {
    private static final JedisPool jedisPool;

    static {
        var compose = TestcontainersInitializer.getComposeContainer();
        var redisHost = compose.getServiceHost("redis", 6379);
        var redisPort = compose.getServicePort("redis", 6379);
        jedisPool = new JedisPool(redisHost, redisPort, "default", "developer");
    }

    private Jedis jedis;

    public void start() {
        jedis = jedisPool.getResource();
    }

    public void stop() {
        jedis.close();
    }

    public Jedis getJedis() {
        return jedis;
    }
}
