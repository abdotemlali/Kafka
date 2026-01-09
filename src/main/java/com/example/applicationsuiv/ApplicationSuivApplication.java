package com.example.applicationsuiv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApplicationSuivApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationSuivApplication.class, args);
    }

}
