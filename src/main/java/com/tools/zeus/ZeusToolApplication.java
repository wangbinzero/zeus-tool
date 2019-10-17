package com.tools.zeus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ZeusToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeusToolApplication.class, args);
    }
}
