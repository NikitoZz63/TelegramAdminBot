package spring.admintgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdminTgBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminTgBotApplication.class, args);
    }
}
