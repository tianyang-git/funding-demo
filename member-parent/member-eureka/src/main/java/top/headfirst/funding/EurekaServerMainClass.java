package top.headfirst.funding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerMainClass {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerMainClass.class, args);
    }
}
