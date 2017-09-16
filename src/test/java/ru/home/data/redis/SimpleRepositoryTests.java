package ru.home.data.redis;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.home.data.redis.entities.Registrator;
import ru.home.data.redis.repository.RegistratorRepository;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class SimpleRepositoryTests<K, V> {

    @Autowired
    RedisOperations<K, V> operations;
    @Autowired
    RegistratorRepository repository;

    @Autowired
    RedisTemplate<K, V> template;

    @Before
//    @After
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
        assertEquals((int)repository.count(), Lists.newArrayList(all).size());
    }

    @Test
    public void findAllByVersion() {
        flushTestRegistrators();
        Iterable<Registrator> all = repository.findByVersion(1);
        all.forEach(System.out::println);
        assertThat(Lists.newArrayList(all).size(), is(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAllByVersionBeetween_NotWorked() {
        flushTestRegistrators();
        Iterable<Registrator> all = repository.findByVersionBetween(1, 3);
        all.forEach(System.out::println);
        assertThat(Lists.newArrayList(all).size(), is(4));
    }

    @Test
    public void sortByIdRegistrator() {
        flushTestRegistrators();
        List<Registrator> byOrderByIdAsc = repository.findAllByOrderByIdAsc();
        byOrderByIdAsc.forEach(System.out::println);
        List<Registrator> byOrderByIdDesc = repository.findAllByOrderByIdDesc();
        byOrderByIdDesc.forEach(System.out::println);
        Collections.reverse(byOrderByIdDesc);

        checkCorrectSorting(byOrderByIdAsc, byOrderByIdDesc);
    }

    @Test
    public void sortByVersionRegistrator() {
        flushTestRegistrators();
        List<Registrator> byOrderByVersionAsc = repository.findAllByOrderByVersionAsc();
        byOrderByVersionAsc.forEach(System.out::println);
        List<Registrator> byOrderByVersionDesc = repository.findAllByOrderByVersionDesc();
        byOrderByVersionDesc.forEach(System.out::println);
        Collections.reverse(byOrderByVersionDesc);

        checkCorrectSorting(byOrderByVersionAsc, byOrderByVersionDesc);
    }

    @Test
    public void getBetweenLastMessageId() {
        System.out.println("test getBetweenLastMessageId");
        flushTestRegistrators();
        final List<Registrator> registratorsByLastMessageId = repository.findAllByLastMessageIdBetween(50000003, 50000006);
        System.out.println(registratorsByLastMessageId.size());
        registratorsByLastMessageId.forEach(System.out::println);
    }

    @Test
    public void getBetweenIdInCustomMode() {
        flushTestRegistrators();
        List<V> users = operations.opsForList().range((K) "registrators", 10000003, 10000005);
        users.forEach(System.out::println);
    }

    private void checkCorrectSorting(List<Registrator> byOrderByIdAsc, List<Registrator> byOrderByIdDesc) {
        Iterator<Registrator> iteratorA = byOrderByIdAsc.iterator();
        Iterator<Registrator> iteratorD = byOrderByIdDesc.iterator();
        while (iteratorA.hasNext() && iteratorD.hasNext()) {
            if (!iteratorA.next().getId().equals(iteratorD.next().getId())) {
                fail("Sorted not correct");
            }
        }
    }


    private int flushTestRegistrators() {
        int capacity = 10;
        List<Registrator> registrators = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            registrators.add(Registrator.builder()
                    .id((long) (10000000 + i))
                    .version(i % (10 / 2))
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
