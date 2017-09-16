package ru.home.data.redis.repository;

import org.springframework.data.repository.CrudRepository;
import ru.home.data.redis.entities.Registrator;

import java.util.List;

/**
 * Created by Zaets Dmitriy on 24.03.2017.
 */
public interface RegistratorRepository extends CrudRepository<Registrator, Long> {

    // by id
    List<Registrator> findAllByOrderByIdAsc();

    List<Registrator> findAllByOrderByIdDesc();

    // by version
    List<Registrator> findByVersion(Integer version);

    List<Registrator> findByVersionBetween(Integer from, Integer to);

    List<Registrator> findAllByOrderByVersionAsc();

    List<Registrator> findAllByOrderByVersionDesc();

    // last message id
    List<Registrator> findAllByLastMessageId(int from, int to);

    List<Registrator> findAllByLastMessageIdBetween(int from, int to);

}