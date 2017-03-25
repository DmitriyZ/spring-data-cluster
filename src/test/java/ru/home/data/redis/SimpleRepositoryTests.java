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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class SimpleRepositoryTests<K, V> {

    @Autowired
    RedisOperations<K, V> operations;
    @Autowired
    RegistratorRepository repository;

    @Before
    @After
    public void setUp() {
        operations.execute((RedisConnection connection) -> {
            connection.flushDb();
            return "OK";
        });
    }

    @Test
    public void findAll() {
        long i = flushTestRegistrators();
        Iterable<Registrator> all = repository.findAll();
        all.forEach(System.out::println);
        assertThat(repository.count(), is(i));
    }

    @Test
    public void sortByIdRegistrator() {
        flushTestRegistrators();
        List<Registrator> byOrderByIdAsc = repository.findByOrderByIdAsc();
        byOrderByIdAsc.forEach(System.out::println);
        List<Registrator> byOrderByIdDesc = repository.findAllByOrderByIdDesc();
        byOrderByIdDesc.forEach(System.out::println);
    }

    @Test
    public void getBetweenLastMessageId() {
        System.out.println("test getBetweenLastMessageId");
        flushTestRegistrators();
        final List<Registrator> registratorsByLastMessageId = repository.findAllByLastMessageId(50000003, 50000006);
        registratorsByLastMessageId.forEach(System.out::println);
    }




    private int flushTestRegistrators() {
        int capacity = 10;
        List<Registrator> registrators = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            registrators.add(Registrator.builder()
                    .id((long) (10000000 + i))
                    .version(i % 10)
                    .lastMessageId(50000000 + i)
                    .lastAuthDate(new Date(i))
                    .allDataSended(i % 2 != 0)
                    .settings(new byte[]{(byte) i, (byte) 0xff}).build()
            );
        }
        repository.save(registrators);
        return registrators.size();
    }
}
