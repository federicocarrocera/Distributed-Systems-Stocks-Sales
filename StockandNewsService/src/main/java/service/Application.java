package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import com.netflix.discovery.EurekaClient;

@SpringBootApplication
public class Application {
    @Autowired
    @Lazy
    private EurekaClient eurekaClient;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }    
}