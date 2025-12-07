package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class ExploreWithMe {
    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMe.class, args);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .build();
    }
}
