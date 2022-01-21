package top.headfirst.funding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testSet(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("funding-redis","test");
    }

    @Test
    public void testExSet(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String s = ops.get("funding-redis");
        System.out.println(s);
    }
}
