package com.jpbazaar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

/**
 * Main entry point for the JPBazaar E-Commerce Application.
 */
@SpringBootApplication(exclude = {
    RedisRepositoriesAutoConfiguration.class
})
public class JpBazaarApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpBazaarApplication.class, args);
    }
}
