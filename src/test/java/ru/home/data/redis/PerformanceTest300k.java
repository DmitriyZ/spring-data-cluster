package ru.home.data.redis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;
import ru.home.data.redis.entities.Registrator;
import ru.home.data.redis.perository.RegistratorRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class PerformanceTest300k<K, V> {

    @Autowired
    RedisOperations<K, V> operations;
    @Autowired
    RegistratorRepository repository;

    @Test
    public void contextLoads() {
        long i = flushTestRegistrators();
        StopWatch sw = new StopWatch();
        sw.start("findall");
        Iterable<Registrator> all = repository.findAll();
        if (all != null) {
            System.out.println("not jvm optimize");
        }
        sw.stop();
        sw.start("repository count");
        assertThat(repository.count(), is(i));
        sw.stop();
        System.out.println(sw.prettyPrint());
    }

    @Before
    @After
    public void setUp() {
        operations.execute((RedisConnection connection) -> {
            connection.flushDb();
            return "OK";
        });
    }


    private int flushTestRegistrators() {
        int capacity = 10000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("create registrators");
        List<Registrator> registrators = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            registrators.add(Registrator.builder()
                    .id((long) (10000000 + i))
                    .version(i % 10)
                    .lastMessageId(50000000 + i)
                    .lastAuthDate(new Date(new Date().getTime() + i * 10 * 1000))
                    .allDataSended(i % 2 != 0)
                    .settings(new byte[]{(byte) i, (byte) 0xff}).build()
            );
        }
        stopWatch.stop();

        stopWatch.start("save in redis " + capacity + " registrators");
        repository.save(registrators);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
        return registrators.size();
    }
}
