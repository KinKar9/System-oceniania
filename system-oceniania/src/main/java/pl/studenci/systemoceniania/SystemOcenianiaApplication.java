package pl.studenci.systemoceniania;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SystemOcenianiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemOcenianiaApplication.class, args);
    }

}
