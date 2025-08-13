package ru.practicum.intershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ru.practicum.intershop")
public class IntershopApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntershopApplication.class, args);
    }

}
