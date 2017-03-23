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
import ru.home.data.redis.entities.Registrator;
import ru.home.data.redis.perository.RegistratorRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class AppTests<K, V> {

    @Autowired
    RedisOperations<K, V> operations;
    @Autowired
    RegistratorRepository repository;

    @Test
    public void contextLoads() {
        long i = flushTestRegistrators();
        Iterable<Registrator> all = repository.findAll();
        all.forEach(System.out::println);
        assertThat(repository.count(), is(i));
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
        Registrator reg1 = Registrator.builder()
                .id(100L)
                .version(1)
                .lastMessageId(12345)
                .lastAuthDate(new Date())
                .allDataSended(true)
                .settings(new byte[]{(byte) 8, (byte) 0xff}).build();
        Registrator reg2 = Registrator.builder()
                .id(200L)
                .version(11)
                .lastMessageId(12345678)
                .lastAuthDate(new Date(0))
                .allDataSended(false)
                .settings(null).build();
        List<Registrator> entities = Arrays.asList(reg1, reg2);
        repository.save(entities);
        return entities.size();
    }
}
