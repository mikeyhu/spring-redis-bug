package buggycache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        NumberService numberService = ctx.getBean(NumberService.class);

        Integer previousResult = null;
        while (true) {
            Integer result = numberService.getCurrentNumber("somekey");
            if (result == null) {
                System.out.println("Received a null that was never set in cache");
                break;
            }
            if (!result.equals(previousResult)) {
                previousResult = result;
                Thread.sleep(900);
            }
        }
    }
}

@Service
class NumberService {

    private Integer number = 0;

    @Cacheable(value = "mycache")
    public Integer getCurrentNumber(String key) {
        number++;
        System.out.println("Will return and cache " + number);
        return number;
    }
}

@Configuration
@EnableCaching
@ComponentScan("buggycache")
class Config {

    private static final String redisHost = "localhost";
    private static final int redisPort = 6379;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }

    @Bean
    RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    CacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
        redisCacheManager.setDefaultExpiration(1);
        return redisCacheManager;
    }
}



