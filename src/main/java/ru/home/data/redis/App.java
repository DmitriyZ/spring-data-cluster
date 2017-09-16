package ru.home.data.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories(basePackages = "ru.home.data.redis.repository")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


}
