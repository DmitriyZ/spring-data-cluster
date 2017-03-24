package ru.home.data.redis.perository;

import org.springframework.data.repository.CrudRepository;
import ru.home.data.redis.entities.Registrator;

import java.util.Date;
import java.util.List;

/**
 * Created by Zaets Dmitriy on 24.03.2017.
 */
public interface RegistratorRepository extends CrudRepository<Registrator, Long> {

    List<Registrator> findByVersion(String version);

    List<Registrator> findByOrderByIdAsc();

    List<Registrator> findAllByOrderByIdDesc();

    List<Registrator> findRegistratorsByLastAuthDateBetween(Date from, Date to);

}