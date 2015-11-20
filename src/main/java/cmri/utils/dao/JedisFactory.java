package cmri.utils.dao;

import cmri.utils.configuration.ConfigManager;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by zhuyin on 3/20/15.
 *
 * Both ShardedJedisPool and JedisPool are intended to be created only once for the entire life of the application. And you destroy them when the application shuts down. Both ShardedJedisPool and JedisPool are thread-safe, meaning that it is safe to use the same instance across threads (in your case requests) and get an instance of ShardedJedis or Jedis. After using ShardedJedis or Jedis, you should return them to the pool, or else the pool will get exhausted (depending on how you configured it).
 */
public class JedisFactory {
    private final JedisPool jedisPool;
    private static final JedisFactory FACTORY = new JedisFactory();
    private JedisFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(ConfigManager.getInt("redis.pool.maxTotal"));
        poolConfig.setMaxIdle(ConfigManager.getInt("redis.pool.maxIdle"));
        poolConfig.setMaxWaitMillis(ConfigManager.getInt("redis.pool.maxWaitMillis"));
        poolConfig.setTestOnBorrow(ConfigManager.getBool("redis.pool.testOnBorrow"));
        poolConfig.setTestOnReturn(ConfigManager.getBool("redis.pool.testOnReturn"));
        String password = ConfigManager.get("redis.password");
        if(StringUtils.isBlank(password)){
            jedisPool = new JedisPool(poolConfig, ConfigManager.get("redis.host"), ConfigManager.getInt("redis.port"));
        }else {
            jedisPool = new JedisPool(poolConfig, ConfigManager.get("redis.host"), ConfigManager.getInt("redis.port"), 0, ConfigManager.get("redis.password", ""));
        }
    }
    public static JedisPool getJedisPool() {
        return FACTORY.jedisPool;
    }
}
